package io.advant.orm.test.service;

import io.advant.orm.DBConnection;
import io.advant.orm.exception.OrmException;
import io.advant.orm.test.dao.BrandDAO;
import io.advant.orm.test.dao.CategoryDAO;
import io.advant.orm.test.dao.ProductDAO;
import io.advant.orm.test.dao.impl.BrandDAOImpl;
import io.advant.orm.test.dao.impl.CategoryDAOImpl;
import io.advant.orm.test.dao.impl.ProductDAOImpl;
import io.advant.orm.test.entity.BrandEntity;
import io.advant.orm.test.entity.CategoryEntity;
import io.advant.orm.test.entity.ProductEntity;
import io.advant.orm.test.testcase.PrintUtil;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marco Romagnolo
 */
public class ProductService {

    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private BrandDAO<BrandEntity> brandDAO;
    private CategoryDAO<CategoryEntity> categoryDAO;
    private ProductDAO<ProductEntity> productDAO;

    public ProductService(DBConnection connection) {
        brandDAO = new BrandDAOImpl(connection);
        categoryDAO = new CategoryDAOImpl(connection);
        productDAO = new ProductDAOImpl(connection);
    }

    public void insert() throws ServiceException {
        try {
            brandDAO.setAutoCommit(false);
            // Insert Brands
            PrintUtil.action("Inserting brands");

            BrandEntity brand = new BrandEntity();
            brand.setId(1000L);
            brand.setName("Brand");
            brandDAO.insert(brand);
            PrintUtil.result("Inserted brand: " + brand);

            BrandEntity brand1 = new BrandEntity();
            brand1.setId(1001L);
            brand1.setName("Brand1");
            brandDAO.insert(brand1);
            PrintUtil.result("Inserted brand: " + brand1);

            BrandEntity brand2 = new BrandEntity();
            brand2.setName("Brand2");
            brandDAO.insert(brand2);
            PrintUtil.result("Inserted brand: " + brand2);

            brandDAO.commit();
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            brandDAO.rollback();
            throw new ServiceException(e);
        }
    }

    public void update() throws ServiceException {
        BrandEntity brand1;
        try {
            brandDAO.setAutoCommit(false);
            brand1 = new BrandEntity();
            brand1.setId(1001L);
            brand1.setName("Brand1_updated");
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            brandDAO.rollback();
            throw new ServiceException(e);
        }
        try {
            brandDAO.update(brand1);
        } catch (OrmException e) {
            Assert.assertTrue("Unsynchronized exception expected", true);
        }
        try {
            BrandEntity brand = brandDAO.find(1000L);

            brand.setName("Brand_updated");
            brandDAO.update(brand);
            PrintUtil.result("Updated brand: " + brand);

            brand.setName("Brand_updated2");
            brandDAO.update(brand);
            PrintUtil.result("Updated brand: " + brand);

            brandDAO.commit();
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            brandDAO.rollback();
            throw new ServiceException(e);
        }
    }

    public void save() throws ServiceException {
        try {
            PrintUtil.action("Save brand");
            BrandEntity brand = new BrandEntity();
            brand.setId(1001L);
            brand.setName("Brand1_updated");
            brandDAO.save(brand);
            Assert.assertNotEquals(new ArrayList<>(), brand);
            PrintUtil.result("Saved brand:" + brand);
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    public void findAll() throws ServiceException {
        try {
            PrintUtil.action("Finding all brands");
            List<BrandEntity> brands = brandDAO.findAll();
            Assert.assertNotEquals(new ArrayList<>(), brands);
            PrintUtil.result("Founded brands:" + brands);
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    public void find() throws ServiceException {
        try {
            PrintUtil.action("Finding brand");
            BrandEntity brand = brandDAO.find(1000L);
            Assert.assertNotNull(brand);
            PrintUtil.result("Found brand:" + brand);
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    public void delete() throws ServiceException {
        try {
            PrintUtil.action("Deleting brand");
            brandDAO.setAutoCommit(false);
            BrandEntity brand = new BrandEntity();
            brand.setId(1000L);
            brandDAO.delete(brand);
            brandDAO.commit();
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            brandDAO.rollback();
            throw new ServiceException(e);
        }
    }

    public void deleteAll() throws ServiceException {
        try {
            PrintUtil.action("Deleting all brands");
            brandDAO.setAutoCommit(false);
            brandDAO.deleteAll();
            brandDAO.commit();
        } catch (OrmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServiceException(e);
        }
    }
}
