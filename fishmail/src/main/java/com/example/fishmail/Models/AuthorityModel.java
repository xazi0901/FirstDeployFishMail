package com.example.fishmail.Models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="authorities")
public class AuthorityModel implements Serializable{
    
    @Id
    @Column(length = 16)
    private String name;

    @Override
    public String toString() {
        return "Authority{" + "name='" + name + "'"
                + "}";
    }
}
