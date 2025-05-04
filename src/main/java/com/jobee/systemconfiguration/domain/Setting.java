package com.jobee.systemconfiguration.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "Id")
    @Getter
    private Long id;

    @Column(name = "Context", nullable = false)
    @Getter
    private String context;

    @Column(name = "Name", nullable = false)
    @Getter
    private String name;

    @Column(name = "Value", nullable = false)
    @Getter
    @Setter
    private String value;

    @Column(name = "Author", nullable = false)
    @Getter
    @Setter
    private String author;

    public Setting() {}

    public Setting(String context, String name, String value, String author) {
        this.context = context;
        this.name = name;
        this.value = value;
    }
}
