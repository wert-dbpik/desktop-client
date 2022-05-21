package ru.wert.datapik.client.entity.serviceQUICK;

import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.entity.serviceREST.ProductService;
import ru.wert.datapik.client.entity.service_interfaces.IProductService;
import ru.wert.datapik.client.exceptions.ItemIsBusyException;
import ru.wert.datapik.client.interfaces.CatalogService;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.client.utils.BLConst.RAZNOE;

public class ProductQuickService implements IProductService , CatalogService<Product> {

    private static ProductQuickService instance;
    private static List<Product> products;
    private static ProductService service = ProductService.getInstance();
    public static Product DEFAULT_PRODUCT;

    private ProductQuickService() {
        reload();
    }

    public static ProductQuickService getInstance() {
        if (instance == null)
            return new ProductQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                products = new ArrayList<>(service.findAll());
                break;
            }
        }
    }


    public Product findByPassportId(Long id) {
        Product foundProduct = null;
        for(Product product : products){
            if(product.getPassport().getId().equals(id)) {
                foundProduct = product;
                break;
            }
        }
        return foundProduct;
    }

    public Product findByName(String name){
        Product foundProduct = null;
        for(Product product : products){
            if(product.getPassport().getName().equals(name)) {
                foundProduct = product;
                break;
            }
        }
        return foundProduct;
    }

    public List<Product> findAllByFolderId(Long id) {
        List<Product> foundProducts = new ArrayList<>();
        for(Product product : products){
            if(product.getFolder().getId().equals(id)) {
                foundProducts.add(product);
            }
        }
        return foundProducts;
    }

    @Override //CatalogService
    public List<Product> findAllByGroupId(Long id) {
        List<Product> foundProducts = new ArrayList<>();
        for(Product product : products){
            if(product.getProductGroup().getId().equals(id)) {
                foundProducts.add(product);
            }
        }
        return foundProducts;
    }


    public Product findById(Long id) {
        Product foundProduct = null;
        for(Product product : products){
            if(product.getId().equals(id)) {
                foundProduct = product;
                break;
            }
        }
        return foundProduct;
    }

    @Override
    public Product save(Product product) {
        Product savedProduct = service.save(product);
        reload();
        return savedProduct;
    }

    @Override
    public boolean update(Product product) {
        boolean res = service.update(product);
        reload();
        return res;
    }

    @Override
    public boolean delete(Product product){
        boolean res = service.delete(product);
        reload();
        AnyPartQuickService.reload();
        PassportQuickService.reload();
        return res;
    }

    public List<Product> findAll() {
        return products;
    }

    public List<Product> findAllByText(String text) {
        List<Product> foundProducts = new ArrayList<>();
        for(Product product : products){
            String name = product.getPassport().getName();
            String decNumber = product.getPassport().getNumber();
            if((name != null && name.contains(text)) || (decNumber != null && decNumber.contains(text))) {
                foundProducts.add(product);
            }
        }
        return foundProducts;
    }

    // ЧЕРТЕЖИ

    @Override
    public List<Draft> findDrafts(Product product) {
        return service.findDrafts(product);
    }

    @Override
    public List<Draft> addDrafts(Product product, Draft draft) {
        List<Draft> res = service.addDrafts(product, draft);
        DraftQuickService.reload();
        reload();
        return res;

    }

    @Override
    public List<Draft> removeDrafts(Product product, Draft draft) {
        List<Draft> res = service.removeDrafts(product, draft);
        DraftQuickService.reload();
        reload();
        return res;
    }

    @Override
    public List<Product> findAllByProductGroup(ProductGroup group) {
        List<Product> foundProducts = new ArrayList<>();
        Long groupId = group.getId();
        for(Product product : products){
            if(product.getProductGroup().getId().equals(groupId)) {
                foundProducts.add(product);
            }
        }
        return foundProducts;
    }
}
