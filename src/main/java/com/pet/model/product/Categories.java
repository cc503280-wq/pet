package com.pet.model.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // 為了避免無窮迴圈
import com.fasterxml.jackson.annotation.JsonIgnore; // 為了避免 JSON 無窮迴圈
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Categories {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, length = 80)
    private String categoryName;

    @OneToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore // ⛔ 絕對要加！轉 JSON 時忽略這個清單，不然會無限迴圈 (Category -> Product -> Category...)
    @ToString.Exclude // ⛔ Lombok 也要排除，不然印 Log 時也會無限迴圈
    private List<Product> products;
}