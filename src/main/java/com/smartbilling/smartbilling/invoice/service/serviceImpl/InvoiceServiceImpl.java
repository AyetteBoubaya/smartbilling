package com.smartbilling.smartbilling.invoice.service.serviceImpl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.smartbilling.smartbilling.customer.domain.Customer;
import com.smartbilling.smartbilling.customer.repository.CustomerRepository;
import com.smartbilling.smartbilling.invoice.domain.*;
import com.smartbilling.smartbilling.invoice.dto.requests.*;
import com.smartbilling.smartbilling.invoice.dto.responses.*;
import com.smartbilling.smartbilling.invoice.repository.InvoiceRepository;
import com.smartbilling.smartbilling.invoice.service.InvoiceService;
import com.smartbilling.smartbilling.product.domain.Product;
import com.smartbilling.smartbilling.product.repository.ProductRepository;
import com.smartbilling.smartbilling.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "client introuvale avec l'id : "+ request.customerId()
                ));
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customer(customer)
                .issueDate(request.issueDate() != null ? request.issueDate() : LocalDate.now())
                .dueDate(request.dueDate())
                .notes(request.notes())
                .status(InvoiceStatus.DRAFT)
                .build();

        List<InvoiceItem> items = request.items().stream()
                .map(itemReq -> buildItem(itemReq, invoice))
                .toList();
        invoice.setItems(items);
        calculateTotals(invoice);

        Invoice saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }


    @Override
    public InvoiceResponse getById(Long id) {
        return toResponse(findById(id));
    }


    @Override
    public Page<InvoiceResponse> getAll(String search, InvoiceStatus status, Long customerId, Pageable pageable) {
        if (customerId != null)
            return invoiceRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
        if (status != null)
            return invoiceRepository.findByStatus(status, pageable).map(this::toResponse);
        if (search != null && !search.isBlank())
            return invoiceRepository.findAll(pageable).map(this::toResponse);
        return invoiceRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse updateStatus(Long id, InvoiceStatusRequest request) {
        Invoice invoice = findById(id);

        //Régle métier sur les transitions de statut
        if (invoice.getStatus() == InvoiceStatus.CANCELLED)
            throw new RuntimeException("Impossible de modifier une facture annulée");
        if (invoice.getStatus() == InvoiceStatus.PAID && request.status() != InvoiceStatus.CANCELLED)
            throw new RuntimeException("Une facture payée ne peut etre annulée");

        invoice.setStatus(request.status());
        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Invoice invoice = findById(id);
        if(invoice.getStatus() != InvoiceStatus.DRAFT)
            throw new RuntimeException("Seules les factures en brouillon peuvent etre supprimées");
        invoiceRepository.delete(invoice);
    }

    @Override
    public byte[] generatePdf(Long id) {
        Invoice invoice = findById(id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer =new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)){

            //Titre
            doc.add(new Paragraph("Facture"))
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);

            //Numéro et date
            doc.add(new Paragraph("N° " + invoice.getInvoiceNumber())
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Date : " + invoice.getIssueDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            // Informations client
            doc.add(new Paragraph("CLIENT").setBold().setFontSize(12));
            doc.add(new Paragraph(invoice.getCustomer().getCompanyName()));
            doc.add(new Paragraph(invoice.getCustomer().getEmail()));

            doc.add(new Paragraph("\n"));

            //Tableau des lignes
            Table table = new Table(UnitValue.createPercentArray(
                    new float[]{40,15,20,10,15}))
                    .useAllAvailableWidth();

            //En tetes
            String[] headers = {"Produit", "Quantité","Prix HT", "TVA", "Total TTC"};
            for (String header: headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            //Lignes
            for(InvoiceItem item : invoice.getItems()){
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(item.getUnitPrice() + " €");
                table.addCell(item.getTaxRate() + "%");
                table.addCell(item.getTotalTTC() + " €");
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            //Totaux
            doc.add(new Paragraph("Total HT :"+ invoice.getTotalHT()+ "€")
                    .setTextAlignment(TextAlignment.RIGHT));
            doc.add(new Paragraph("Total TVA : "+ invoice.getTotalTVA()+ "€")
                    .setTextAlignment(TextAlignment.RIGHT));
            doc.add(new Paragraph("total TTC :"+invoice.getTotalTTC()+ "€")
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));
        } catch (Exception e){
            throw new RuntimeException("Erreur lors de la géneration du PDF", e);
        }


        return baos.toByteArray();
    }


    // -------Helpers

    private InvoiceItem buildItem(InvoiceItemRequest itemReq, Invoice invoice) {
        Product product = productRepository.findById(itemReq.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable avec l'id : " + itemReq.productId()));

        BigDecimal unitPrice = itemReq.unitPrice() != null
                ? itemReq.unitPrice()
                : product.getPriceHT();

        InvoiceItem item = InvoiceItem.builder()
                .invoice(invoice)
                .product(product)
                .quantity(itemReq.quantity())
                .unitPrice(unitPrice)
                .taxRate(product.getTaxRate())
                .totalHT(BigDecimal.ZERO)
                .totalTTC(BigDecimal.ZERO)
                .build();

        item.calculateTotals();
        return item;
    }

    private String generateInvoiceNumber() {
        int year = Year.now().getValue();
        String prefix = "INV-" + year + "-";
        long count = invoiceRepository.countByInvoiceNumberStartingWith(prefix);
        return String.format("%s%04d", prefix, count + 1);
        //-> INV-2024-0001, INV-2024-0002...
    }

    private void calculateTotals(Invoice invoice) {
        BigDecimal totalHT = invoice.getItems().stream()
                .map(InvoiceItem::getTotalHT)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTTC = invoice.getItems().stream()
                .map(InvoiceItem::getTotalTTC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTVA = totalTTC.subtract(totalHT)
                .setScale(2, RoundingMode.HALF_UP);


        invoice.setTotalHT(totalHT.setScale(2, RoundingMode.HALF_UP));
        invoice.setTotalTVA(totalTVA);
        invoice.setTotalTTC(totalTTC.setScale(2, RoundingMode.HALF_UP));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(invoice.getCustomer().getId())
                .customerName(invoice.getCustomer().getCompanyName())
                .items(invoice.getItems().stream().map(this::toItemResponse).toList())
                .status(invoice.getStatus())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .totalHT(invoice.getTotalHT())
                .totalTVA(invoice.getTotalTVA())
                .totalTTC(invoice.getTotalTTC())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .build();

    }

    private InvoiceItemResponse toItemResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .taxRate(item.getTaxRate())
                .totalHt(item.getTotalHT())
                .totalTTC(item.getTotalTTC())
                .build();
    }

    private Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facture introuvable avec l'id : "+ id
                ));
    }

}