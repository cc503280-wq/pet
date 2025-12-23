package com.pet.model.product;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class CategoriesBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CATEGORY_ID")
	private Integer categoryId;

	@Column(name = "CATEGORY_NAME" , nullable = false)
	private String categoryName;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private Set<ProductBean> products = new LinkedHashSet<>();

	public CategoriesBean() {
	}
	
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategory_name(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<ProductBean> getProducts() {
		return products;
	}

	public void setProducts(Set<ProductBean> products) {
		this.products = products;
	}

}