package com.jobee.systemconfiguration.api.controllers;

import com.jobee.systemconfiguration.application.ErrorCodes;
import com.jobee.systemconfiguration.application.SettingService;
import com.jobee.systemconfiguration.application.validators.SettingValidator;
import com.jobee.systemconfiguration.contracts.SettingModel;
import com.jobee.systemconfiguration.contracts.SettingUpsertModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;
    private final SettingValidator settingValidator;

    public SettingController(SettingService settingService, SettingValidator settingValidator) {
        this.settingService = settingService;
        this.settingValidator = settingValidator;
    }

    @GetMapping("{context}/{name}")
    @Operation(summary = "Get setting for a given timestamp", description = "Get setting for a given timestamp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SettingModel.class))}
            ),
            @ApiResponse(responseCode = "404",
                    description = "Setting not found",
                    content = {@Content(schema = @Schema())}),
            })
    public ResponseEntity<SettingModel> get(@PathVariable("context") @Max(255) String context,
                                            @PathVariable("name") @Max(255) String name,
                                            @RequestParam(value = "timestamp", required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                          LocalDateTime timestamp) {
        SettingModel setting = settingService.getSetting(context, name, timestamp);

        return ResponseEntity.ok(setting);
    }

    @GetMapping("{context}")
    @Operation(summary = "Get setting for a given timestamp", description = "Get settings for a given timestamp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SettingModel[].class))}
            )
    })
    public ResponseEntity<?> getAll(@PathVariable("context") @Max(255) String context,
                                            @RequestParam("names") Collection<String> names,
                                            @RequestParam(value = "timestamp", required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            LocalDateTime timestamp) {

        if (names == null || names.isEmpty()) {
            ObjectError[] errors = {
                    new ObjectError("names", new String[] {ErrorCodes.REQUIRED}, null, "Names are required")
            };

            return ResponseEntity.badRequest().body(errors);
        }

        Collection<SettingModel> settings = settingService.getSettings(context, names, timestamp);

        return ResponseEntity.ok(settings);
    }

    @PostMapping(value = "{context}/{name}", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SettingModel.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request",
                    content = {@Content(schema = @Schema(implementation = ObjectError[].class))}),
    })
    public ResponseEntity<?> create(@PathVariable("context") String context,
                                    @PathVariable("name") String name,
                                    @RequestBody SettingUpsertModel request) {

        Errors errors = new BeanPropertyBindingResult(request, "request");
        settingValidator.validate(request, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        SettingModel dto = new SettingModel(context, name, request.value());

        settingService.create(dto);

        return ResponseEntity.created(URI.create("/settings/" + context + "/" + name))
                .body(dto);
    }

    @PutMapping("{context}/{name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema())}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request",
                    content = {@Content(schema = @Schema(implementation = ObjectError[].class))}),
    })
    public ResponseEntity<?> update(@PathVariable("context") String context,
                                    @PathVariable("name") String name,
                                    @RequestBody SettingUpsertModel request) {

        Errors errors = new BeanPropertyBindingResult(request, "request");
        settingValidator.validate(request, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        SettingModel dto = new SettingModel(context, name, request.value());

        settingService.update(dto);
        return ResponseEntity.noContent().build();
    }
}
