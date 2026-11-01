package com.groceryai.backend.service;

import com.groceryai.backend.dto.CreateSaleRequest;
import com.groceryai.backend.dto.SaleResponse;
import com.groceryai.backend.dto.UpdateSaleRequest;
import com.groceryai.backend.entity.Customer;
import com.groceryai.backend.entity.Employee;
import com.groceryai.backend.entity.Product;
import com.groceryai.backend.entity.Sale;
import com.groceryai.backend.repository.CustomerRepository;
import com.groceryai.backend.repository.EmployeeRepository;
import com.groceryai.backend.repository.ProductRepository;
import com.groceryai.backend.repository.SaleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SaleService {

    private final SaleRepository saleRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public SaleService(
            SaleRepository saleRepository,
            EmployeeRepository employeeRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.saleRepository = saleRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SaleResponse getSaleById(Long id) {
        return toResponse(findSale(id));
    }

    public List<SaleResponse> getSalesByCustomer(Integer customerId) {
        return saleRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SaleResponse> getSalesByEmployee(Integer employeeId) {
        return saleRepository.findBySalesperson_EmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SaleResponse> getSalesByProduct(Integer productId) {
        return saleRepository.findByProduct_ProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        validateSaleData(
                request.salesId(),
                request.salespersonId(),
                request.customerId(),
                request.productId(),
                request.quantity(),
                request.discount(),
                request.transactionNumber()
        );

        if (saleRepository.existsById(request.salesId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Sale already exists with ID: " + request.salesId());
        }

        Employee employee = findEmployee(request.salespersonId());
        Customer customer = findCustomer(request.customerId());
        Product product = findProduct(request.productId());

        BigDecimal totalPrice = calculateTotalPrice(
                product,
                request.quantity(),
                request.discount()
        );

        Sale sale = new Sale(
                request.salesId(),
                employee,
                customer,
                product,
                request.quantity(),
                normalizeDiscount(request.discount()),
                totalPrice,
                request.salesDate(),
                normalizeOptional(request.transactionNumber())
        );

        return toResponse(saleRepository.saveAndFlush(sale));
    }

    @Transactional
    public SaleResponse updateSale(Long id, UpdateSaleRequest request) {
        validateSaleData(
                id,
                request.salespersonId(),
                request.customerId(),
                request.productId(),
                request.quantity(),
                request.discount(),
                request.transactionNumber()
        );

        Sale sale = findSale(id);

        Employee employee = findEmployee(request.salespersonId());
        Customer customer = findCustomer(request.customerId());
        Product product = findProduct(request.productId());

        BigDecimal totalPrice = calculateTotalPrice(
                product,
                request.quantity(),
                request.discount()
        );

        sale.update(
                employee,
                customer,
                product,
                request.quantity(),
                normalizeDiscount(request.discount()),
                totalPrice,
                request.salesDate(),
                normalizeOptional(request.transactionNumber())
        );

        return toResponse(saleRepository.saveAndFlush(sale));
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = findSale(id);
        saleRepository.delete(sale);
        saleRepository.flush();
    }

    private BigDecimal calculateTotalPrice(
            Product product,
            Integer quantity,
            BigDecimal discount) {

        BigDecimal productPrice = product.getPrice();

        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot create a sale because the product has no valid price.");
        }

        BigDecimal effectiveDiscount = normalizeDiscount(discount);

        BigDecimal discountedPrice = productPrice.multiply(
                BigDecimal.ONE.subtract(effectiveDiscount)
        );

        return discountedPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeDiscount(BigDecimal discount) {
        return discount == null ? BigDecimal.ZERO : discount;
    }

    private Sale findSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sale not found with ID: " + id));
    }

    private Employee findEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found with ID: " + employeeId));
    }

    private Customer findCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + customerId));
    }

    private Product findProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + productId));
    }

    private void validateSaleData(
            Long salesId,
            Integer salespersonId,
            Integer customerId,
            Integer productId,
            Integer quantity,
            BigDecimal discount,
            String transactionNumber) {

        if (salesId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sales ID is required.");
        }

        if (salespersonId == null || customerId == null || productId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salesperson ID, customer ID and product ID are required.");
        }

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero.");
        }

        if (discount != null &&
                (discount.compareTo(BigDecimal.ZERO) < 0 ||
                 discount.compareTo(BigDecimal.ONE) > 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount must be between 0 and 1.");
        }

        if (transactionNumber != null &&
                transactionNumber.trim().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction number cannot exceed 50 characters.");
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private SaleResponse toResponse(Sale sale) {
        String salespersonName =
                sale.getSalesperson().getFirstName() + " " +
                sale.getSalesperson().getLastName();

        String customerName =
                sale.getCustomer().getFirstName() + " " +
                sale.getCustomer().getLastName();

        return new SaleResponse(
                sale.getSalesId(),
                sale.getSalesperson().getEmployeeId(),
                salespersonName,
                sale.getCustomer().getCustomerId(),
                customerName,
                sale.getProduct().getProductId(),
                sale.getProduct().getProductName(),
                sale.getQuantity(),
                sale.getDiscount(),
                sale.getTotalPrice(),
                sale.getSalesDate(),
                sale.getTransactionNumber()
        );
    }
}