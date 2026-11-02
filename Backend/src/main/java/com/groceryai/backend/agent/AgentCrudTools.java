
package com.groceryai.backend.agent;

import com.groceryai.backend.dto.*;
import com.groceryai.backend.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class AgentCrudTools {

    private static final Logger log =
            LoggerFactory.getLogger(AgentCrudTools.class);

    private final CategoryService categoryService;
    private final ProductService productService;
    private final CountryService countryService;
    private final CityService cityService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final SaleService saleService;
    private final AgentMutationConfirmationService confirmationService;

    public AgentCrudTools(
            CategoryService categoryService,
            ProductService productService,
            CountryService countryService,
            CityService cityService,
            CountryService countryServiceDuplicate,
            CustomerService customerService,
            EmployeeService employeeService,
            SaleService saleService,
            AgentMutationConfirmationService confirmationService
    ) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.countryService = countryService;
        this.cityService = cityService;
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.saleService = saleService;
        this.confirmationService = confirmationService;
    }

    private int safePage(Integer page) {
        return page == null ? 0 : Math.max(0, page);
    }

    private int safeLimit(Integer limit) {
        return limit == null ? 10 : Math.max(1, Math.min(limit, 20));
    }

    // =========================================================
    // CATEGORIES
    // =========================================================

    @Tool(description = "List categories. Supports pagination using page and limit. Default limit is 10; maximum is 20.")
    public Object getCategories(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCategories");
        return categoryService.getCategoriesForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one category using its category ID.")
    public Object getCategoryById(Integer categoryId) {
        log.info("AI TOOL CALLED: getCategoryById");
        return categoryService.getCategoryById(categoryId);
    }

    @Tool(description = "Create a category. This only prepares the operation and returns a confirmation token. Do not claim the category was created until the user confirms.")
    public Object createCategory(CreateCategoryRequest request) {
        log.info("AI TOOL CALLED: createCategory");
        return confirmationService.prepare(
                "Create category: " + request,
                () -> categoryService.createCategory(request));
    }

    @Tool(description = "Update a category. This requires confirmation before execution.")
    public Object updateCategory(Integer categoryId, UpdateCategoryRequest request) {
        log.info("AI TOOL CALLED: updateCategory");
        return confirmationService.prepare(
                "Update category " + categoryId + ": " + request,
                () -> categoryService.updateCategory(categoryId, request));
    }

    @Tool(description = "Delete a category. This requires confirmation before execution.")
    public Object deleteCategory(Integer categoryId) {
        log.info("AI TOOL CALLED: deleteCategory");
        return confirmationService.prepare(
                "Delete category " + categoryId,
                () -> {
                    categoryService.deleteCategory(categoryId);
                    return "Category " + categoryId + " deleted successfully.";
                });
    }

    // =========================================================
    // PRODUCTS
    // =========================================================

    @Tool(description = "List products. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getProducts(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getProducts");
        return productService.getProductsForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one product using its product ID.")
    public Object getProductById(Integer productId) {
        log.info("AI TOOL CALLED: getProductById");
        return productService.getProductById(productId);
    }

    @Tool(description = "List products belonging to a category. Supports pagination.")
    public Object getProductsByCategory(Integer categoryId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getProductsByCategory");
        return productService.getProductsByCategoryForAgent(
                categoryId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "Create a product. Requires user confirmation before execution.")
    public Object createProduct(CreateProductRequest request) {
        log.info("AI TOOL CALLED: createProduct");
        return confirmationService.prepare(
                "Create product: " + request,
                () -> productService.createProduct(request));
    }

    @Tool(description = "Update a product. Requires user confirmation before execution.")
    public Object updateProduct(Integer productId, UpdateProductRequest request) {
        log.info("AI TOOL CALLED: updateProduct");
        return confirmationService.prepare(
                "Update product " + productId + ": " + request,
                () -> productService.updateProduct(productId, request));
    }

    @Tool(description = "Delete a product. Requires user confirmation before execution.")
    public Object deleteProduct(Integer productId) {
        log.info("AI TOOL CALLED: deleteProduct");
        return confirmationService.prepare(
                "Delete product " + productId,
                () -> {
                    productService.deleteProduct(productId);
                    return "Product " + productId + " deleted successfully.";
                });
    }

    // =========================================================
    // COUNTRIES
    // =========================================================

    @Tool(description = "List countries. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getCountries(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCountries");
        return countryService.getCountriesForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one country using its country ID.")
    public Object getCountryById(Integer countryId) {
        log.info("AI TOOL CALLED: getCountryById");
        return countryService.getCountryById(countryId);
    }

    @Tool(description = "Create a country. Requires user confirmation before execution.")
    public Object createCountry(CreateCountryRequest request) {
        log.info("AI TOOL CALLED: createCountry");
        return confirmationService.prepare(
                "Create country: " + request,
                () -> countryService.createCountry(request));
    }

    @Tool(description = "Update a country. Requires user confirmation before execution.")
    public Object updateCountry(Integer countryId, UpdateCountryRequest request) {
        log.info("AI TOOL CALLED: updateCountry");
        return confirmationService.prepare(
                "Update country " + countryId + ": " + request,
                () -> countryService.updateCountry(countryId, request));
    }

    @Tool(description = "Delete a country. Requires user confirmation before execution.")
    public Object deleteCountry(Integer countryId) {
        log.info("AI TOOL CALLED: deleteCountry");
        return confirmationService.prepare(
                "Delete country " + countryId,
                () -> {
                    countryService.deleteCountry(countryId);
                    return "Country " + countryId + " deleted successfully.";
                });
    }

    // =========================================================
    // CITIES
    // =========================================================

    @Tool(description = "List cities. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getCities(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCities");
        return cityService.getCitiesForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one city using its city ID.")
    public Object getCityById(Integer cityId) {
        log.info("AI TOOL CALLED: getCityById");
        return cityService.getCityById(cityId);
    }

    @Tool(description = "List cities belonging to a country. Supports pagination.")
    public Object getCitiesByCountry(Integer countryId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCitiesByCountry");
        return cityService.getCitiesByCountryForAgent(
                countryId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "Create a city. Requires user confirmation before execution.")
    public Object createCity(CreateCityRequest request) {
        log.info("AI TOOL CALLED: createCity");
        return confirmationService.prepare(
                "Create city: " + request,
                () -> cityService.createCity(request));
    }

    @Tool(description = "Update a city. Requires user confirmation before execution.")
    public Object updateCity(Integer cityId, UpdateCityRequest request) {
        log.info("AI TOOL CALLED: updateCity");
        return confirmationService.prepare(
                "Update city " + cityId + ": " + request,
                () -> cityService.updateCity(cityId, request));
    }

    @Tool(description = "Delete a city. Requires user confirmation before execution.")
    public Object deleteCity(Integer cityId) {
        log.info("AI TOOL CALLED: deleteCity");
        return confirmationService.prepare(
                "Delete city " + cityId,
                () -> {
                    cityService.deleteCity(cityId);
                    return "City " + cityId + " deleted successfully.";
                });
    }

    // =========================================================
    // CUSTOMERS
    // =========================================================

    @Tool(description = "List customers. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getCustomers(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCustomers");
        return customerService.getCustomersForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one customer using their customer ID.")
    public Object getCustomerById(Integer customerId) {
        log.info("AI TOOL CALLED: getCustomerById");
        return customerService.getCustomerById(customerId);
    }

    @Tool(description = "List customers belonging to a city. Supports pagination.")
    public Object getCustomersByCity(Integer cityId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getCustomersByCity");
        return customerService.getCustomersByCityForAgent(
                cityId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "Create a customer. Requires user confirmation before execution.")
    public Object createCustomer(CreateCustomerRequest request) {
        log.info("AI TOOL CALLED: createCustomer");
        return confirmationService.prepare(
                "Create customer: " + request,
                () -> customerService.createCustomer(request));
    }

    @Tool(description = "Update a customer. Requires user confirmation before execution.")
    public Object updateCustomer(Integer customerId, UpdateCustomerRequest request) {
        log.info("AI TOOL CALLED: updateCustomer");
        return confirmationService.prepare(
                "Update customer " + customerId + ": " + request,
                () -> customerService.updateCustomer(customerId, request));
    }

    @Tool(description = "Delete a customer. Requires user confirmation before execution.")
    public Object deleteCustomer(Integer customerId) {
        log.info("AI TOOL CALLED: deleteCustomer");
        return confirmationService.prepare(
                "Delete customer " + customerId,
                () -> {
                    customerService.deleteCustomer(customerId);
                    return "Customer " + customerId + " deleted successfully.";
                });
    }

    // =========================================================
    // EMPLOYEES
    // =========================================================

    @Tool(description = "List employees. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getEmployees(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getEmployees");
        return employeeService.getEmployeesForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one employee using their employee ID.")
    public Object getEmployeeById(Integer employeeId) {
        log.info("AI TOOL CALLED: getEmployeeById");
        return employeeService.getEmployeeById(employeeId);
    }

    @Tool(description = "List employees belonging to a city. Supports pagination.")
    public Object getEmployeesByCity(Integer cityId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getEmployeesByCity");
        return employeeService.getEmployeesByCityForAgent(
                cityId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "Create an employee. Requires user confirmation before execution.")
    public Object createEmployee(CreateEmployeeRequest request) {
        log.info("AI TOOL CALLED: createEmployee");
        return confirmationService.prepare(
                "Create employee: " + request,
                () -> employeeService.createEmployee(request));
    }

    @Tool(description = "Update an employee. Requires user confirmation before execution.")
    public Object updateEmployee(Integer employeeId, UpdateEmployeeRequest request) {
        log.info("AI TOOL CALLED: updateEmployee");
        return confirmationService.prepare(
                "Update employee " + employeeId + ": " + request,
                () -> employeeService.updateEmployee(employeeId, request));
    }

    @Tool(description = "Delete an employee. Requires user confirmation before execution.")
    public Object deleteEmployee(Integer employeeId) {
        log.info("AI TOOL CALLED: deleteEmployee");
        return confirmationService.prepare(
                "Delete employee " + employeeId,
                () -> {
                    employeeService.deleteEmployee(employeeId);
                    return "Employee " + employeeId + " deleted successfully.";
                });
    }

    // =========================================================
    // SALES
    // =========================================================

    @Tool(description = "List individual sales records. Use this for requests to show, list, or inspect sales, not analytics summaries. Supports pagination. Default limit is 10; maximum is 20.")
    public Object getSales(Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getSales");
        return saleService.getSalesForAgent(
                safePage(page), safeLimit(limit));
    }

    @Tool(description = "Get one individual sale using its sales ID.")
    public Object getSaleById(Long salesId) {
        log.info("AI TOOL CALLED: getSaleById");
        return saleService.getSaleById(salesId);
    }

    @Tool(description = "Get the latest individual sale in the database.")
    public Object getLatestSale() {
        log.info("AI TOOL CALLED: getLatestSale");
        return saleService.getLatestSale();
    }

    @Tool(description = "List sales belonging to a customer. Supports pagination.")
    public Object getSalesByCustomer(Integer customerId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getSalesByCustomer");
        return saleService.getSalesByCustomerForAgent(
                customerId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "List sales made by an employee. Supports pagination.")
    public Object getSalesByEmployee(Integer employeeId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getSalesByEmployee");
        return saleService.getSalesByEmployeeForAgent(
                employeeId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "List sales involving a product. Supports pagination.")
    public Object getSalesByProduct(Integer productId, Integer page, Integer limit) {
        log.info("AI TOOL CALLED: getSalesByProduct");
        return saleService.getSalesByProductForAgent(
                productId, safePage(page), safeLimit(limit));
    }

    @Tool(description = "Create a sale. Requires user confirmation before execution.")
    public Object createSale(CreateSaleRequest request) {
        log.info("AI TOOL CALLED: createSale");
        return confirmationService.prepare(
                "Create sale: " + request,
                () -> saleService.createSale(request));
    }

    @Tool(description = "Update a sale. Requires user confirmation before execution.")
    public Object updateSale(Long salesId, UpdateSaleRequest request) {
        log.info("AI TOOL CALLED: updateSale");
        return confirmationService.prepare(
                "Update sale " + salesId + ": " + request,
                () -> saleService.updateSale(salesId, request));
    }

    @Tool(description = "Delete a sale. Requires user confirmation before execution.")
    public Object deleteSale(Long salesId) {
        log.info("AI TOOL CALLED: deleteSale");
        return confirmationService.prepare(
                "Delete sale " + salesId,
                () -> {
                    saleService.deleteSale(salesId);
                    return "Sale " + salesId + " deleted successfully.";
                });
    }
}
