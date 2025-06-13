package com.inn.cafe.pojo;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "bill")
public class Bill implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private Integer id;

    @Column( name = "uuid" )
    private String uuid;

    @Column( name = "name" )
    private String name;

    @Column( name = "email" )
    private String email;

    @Column( name = "contactNumber" )
    private String contactNumber;

    @Column( name = "paymentMethod" )
    private String paymentMethod;

    @Column( name = "total" )
    private Integer total;

    @Column( name = "productDetails", columnDefinition = "json")
    @Type(JsonType.class)
    private String productDetails;

    @Column( name = "createdBy" )
    private String createdBy;

}
