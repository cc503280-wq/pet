package com.pet.dao.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import com.pet.model.product.CategoriesBean;
import com.pet.model.product.ProductBean;
import com.pet.model.product.ProductImagesBean;

public class ProductDao {

	private Session session;

	public ProductDao(Session session) {
		this.session = session;
	}

	// 查詢所有商品
	public List<ProductBean> getAllProducts() {
		List<ProductBean> products = new ArrayList<>();
		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category WHERE p.isActive = true";

			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			products = query.getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return products;
	}

	// 查詢所有下架商品
	public List<ProductBean> NotOnShelf() {
		List<ProductBean> products = new ArrayList<>();
		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category WHERE p.isActive = false";

			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			products = query.getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return products;
	}

	// 查詢類別商品
	public List<ProductBean> getCategorySearch(String id) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		if (id == null) {
			return products;
		}

		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category WHERE p.category.categoryId = :cid AND p.isActive = true";
			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			query.setParameter("cid", Integer.parseInt(id));

			products = query.getResultList();
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
		return products;
	}

	// 查詢類別下架商品
	public List<ProductBean> getCategoryNotOnShelfSearch(String id) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		if (id == null) {
			return products;
		}

		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category WHERE p.category.categoryId = :cid AND p.isActive = false";
			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			query.setParameter("cid", Integer.parseInt(id));

			products = query.getResultList();
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
		return products;
	}

	// 模糊查詢
	public List<ProductBean> getProductsByKeyword(String keyword) {
		List<ProductBean> products = new ArrayList<>();

		if (keyword == null) {
			return products;
		}

		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category "
					+ "WHERE p.productName LIKE :kw AND p.isActive = true";
			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			query.setParameter("kw", "%" + keyword + "%");

			products = query.getResultList();
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}

		return products;
	}

	// 下架模糊查詢
	public List<ProductBean> getProductsByKeywordNotOnShelf(String keyword) {
		List<ProductBean> products = new ArrayList<>();

		if (keyword == null) {
			return products;
		}

		try {
			session.beginTransaction();

			String hql = "FROM ProductBean p JOIN FETCH p.category "
					+ "WHERE p.productName LIKE :kw AND p.isActive = false";
			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			query.setParameter("kw", "%" + keyword + "%");

			products = query.getResultList();
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}

		return products;
	}

	// 查詢單筆
	public ProductBean getProductById(String id) {
		ProductBean product = null;

		if (id == null || id.trim().isEmpty()) {
			return null;
		}

		try {
			session.beginTransaction();

			Integer productId = Integer.parseInt(id);

			String hql = "FROM ProductBean p JOIN FETCH p.category WHERE p.productId = :pid";
			Query<ProductBean> query = session.createQuery(hql, ProductBean.class);
			query.setParameter("pid", productId);

			product = query.uniqueResult();
			session.getTransaction().commit();

		} catch (NumberFormatException e) {
			System.out.println("錯誤：傳入的 ID 不是數字 - " + id);
		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}

		return product;
	}

	// 上架商品
	public void SetActived(String id) {
		if (id == null)
			return;
		try {
			session.beginTransaction();

			String hql = "UPDATE ProductBean p SET p.isActive = true WHERE p.productId = :id";

			Query query = (Query) session.createMutationQuery(hql); // Hibernate 6 建議用 createMutationQuery，舊版用
																	// createQuery
			query.setParameter("id", Integer.parseInt(id));

			int resultCount = query.executeUpdate();

			session.getTransaction().commit();

			System.out.println("成功上架 " + resultCount + " 筆商品");

		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}

	// 上架商品
	public void SetNotActived(String id) {
		if (id == null)
			return;
		try {
			session.beginTransaction();

			String hql = "UPDATE ProductBean p SET p.isActive = false WHERE p.productId = :id";

			Query query = (Query) session.createMutationQuery(hql); // Hibernate 6 建議用 createMutationQuery，舊版用
																	// createQuery
			query.setParameter("id", Integer.parseInt(id));

			int resultCount = query.executeUpdate();

			session.getTransaction().commit();

			System.out.println("成功上架 " + resultCount + " 筆商品");

		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}

	// 新增圖片
	public void setImage(ProductBean product) {
		// 1. 防呆
		if (product == null || product.getImageUrl() == null) {
			return;
		}

		try {
			session.beginTransaction();

			String sql = "{call AddProductImage(:pid, :url)}";

			MutationQuery query = session.createNativeMutationQuery(sql);

			query.setParameter("pid", product.getProductId());
			query.setParameter("url", product.getImageUrl());

			query.executeUpdate();

			session.getTransaction().commit();

			System.out.println("圖片新增成功，產品ID: " + product.getProductId());

		} catch (Exception e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}
	
	//新增商品
	public ProductBean addProduct(String pname, String pdes, String price, String stock, String categoryid,
	        String imageurl, String categoryname, String expiredate, String is_active) {
	    
	    ProductBean product = new ProductBean();

	    try {
	        session.beginTransaction();

	        product.setProductName(pname);
	        product.setDescription(pdes);
	        product.setPrice(new BigDecimal(price)); 
	        product.setStock(Integer.parseInt(stock));
	        product.setImageUrl(imageurl);
	        product.setExpireDate(expiredate);
	        product.setIsActive(!Boolean.parseBoolean(is_active));
	        Integer cId = Integer.parseInt(categoryid);
	        CategoriesBean category = session.getReference(CategoriesBean.class, cId);
	        
	        product.setCategory(category); 
	        session.persist(product);

	        session.getTransaction().commit();
	        System.out.println("新增成功，商品 ID: " + product.getProductId());

	    } catch (Exception e) {
	        if (session.getTransaction() != null && session.getTransaction().isActive()) {
	            session.getTransaction().rollback();
	        }
	        e.printStackTrace();
	        return null; 
	    }

	    return product;
	}
	
	//更新商品
	public ProductBean updateProduct(String pname, String pdes, String price, String stock, String categoryid, String image,
	        String categoryname, String expiredate, String is_active, String id) {
	    
	    ProductBean product = null;

	    try {
	        session.beginTransaction();

	        // 1. 先把舊資料抓出來 (Persistent 狀態)
	        Integer pId = Integer.parseInt(id);
	        product = session.find(ProductBean.class, pId);

	        if (product != null) {
	            BigDecimal priceBD = new BigDecimal(price);
	            int stockInt = Integer.parseInt(stock);
	            
	            boolean isActiveBool = "1".equals(is_active); 

	            if (stockInt <= 0) {
	                isActiveBool = false;
	            }

	            product.setProductName(pname);
	            product.setDescription(pdes);
	            product.setPrice(priceBD);
	            product.setStock(stockInt);
	            product.setImageUrl(image);
	            product.setExpireDate(expiredate);
	            product.setIsActive(isActiveBool);
	            Integer cId = Integer.parseInt(categoryid);
	            CategoriesBean newCategory = session.getReference(CategoriesBean.class, cId);
	            product.setCategory(newCategory);
	            if (product.getProductImages() != null) {
	                for (ProductImagesBean img : product.getProductImages()) {
	                    // 找到主圖 (Sort Order = 1)
	                    if (img.getSortOrder() != null && img.getSortOrder() == 1) {
	                        img.setImageUrl(image);
	                        break; 
	                    }
	                }
	            }
	        }

	        session.getTransaction().commit();

	    } catch (Exception e) {
	        if (session.getTransaction() != null && session.getTransaction().isActive()) {
	            session.getTransaction().rollback();
	        }
	        e.printStackTrace();
	        return null;
	    }

	    return product;
	}
	
}
