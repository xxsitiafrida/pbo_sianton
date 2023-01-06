package com.example.application.views.dataadmin;

import com.example.application.data.entity.Dataadmin;
import com.example.application.data.service.DataadminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Data Administrator")
@Route(value = "Data-Admin/:dataadminID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DataAdminView extends Div implements BeforeEnterObserver {

    private final String DATAADMIN_ID = "dataadminID";
    private final String DATAADMIN_EDIT_ROUTE_TEMPLATE = "Data-Admin/%s/edit";

    private final Grid<Dataadmin> grid = new Grid<>(Dataadmin.class, false);

    private Upload gambar;
    private Image gambarPreview;
    private TextField nama;
    private TextField email;
    private TextField nomorTelepon;
    private TextField jenisKelamin;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final BeanValidationBinder<Dataadmin> binder;

    private Dataadmin dataadmin;

    private final DataadminService dataadminService;

    public DataAdminView(DataadminService dataadminService) {
        this.dataadminService = dataadminService;
        addClassNames("data-admin-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<Dataadmin> gambarRenderer = LitRenderer.<Dataadmin>of(
                "<span style='border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; width: 64px; height: 64px'><img style='max-width: 100%' src=${item.gambar} /></span>")
                .withProperty("gambar", item -> {
                    if (item != null && item.getGambar() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getGambar());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(gambarRenderer).setHeader("Gambar").setWidth("96px").setFlexGrow(0);

        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("nomorTelepon").setAutoWidth(true);
        grid.addColumn("jenisKelamin").setAutoWidth(true);

        grid.setItems(query -> dataadminService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DATAADMIN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DataAdminView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Dataadmin.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(gambar, gambarPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.dataadmin == null) {
                    this.dataadmin = new Dataadmin();
                    binder.writeBean(this.dataadmin);
                    dataadminService.update(this.dataadmin);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah ditambahkan");
                    UI.getCurrent().navigate(DataAdminView.class);
                }else {
                    binder.writeBean(this.dataadmin);
                    dataadminService.update(this.dataadmin);
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah diperbaharui");
                    UI.getCurrent().navigate(DataAdminView.class);}
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
        delete.addClickListener(e -> {
            try {
                if (this.dataadmin == null){
                    Notification.show("Tidak ada akun admin");
                } else {
                    binder.writeBean(this.dataadmin);
                    dataadminService.delete(this.dataadmin.getId());
                    clearForm();
                    refreshGrid();
                    Notification.show("Data telah terhapus");
                    UI.getCurrent().navigate(DataAdminView.class);
                }

            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error Deleting the data.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to delete");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> dataadminId = event.getRouteParameters().get(DATAADMIN_ID).map(Long::parseLong);
        if (dataadminId.isPresent()) {
            Optional<Dataadmin> dataadminFromBackend = dataadminService.get(dataadminId.get());
            if (dataadminFromBackend.isPresent()) {
                populateForm(dataadminFromBackend.get());
            } else {
                Notification.show(String.format("The requested dataadmin was not found, ID = %s", dataadminId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DataAdminView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        Label gambarLabel = new Label("Gambar");
        gambarPreview = new Image();
        gambarPreview.setWidth("100%");
        gambar = new Upload();
        gambar.getStyle().set("box-sizing", "border-box");
        gambar.getElement().appendChild(gambarPreview.getElement());
        nama = new TextField("Nama");
        email = new TextField("Email");
        nomorTelepon = new TextField("Nomor Telepon");
        jenisKelamin = new TextField("Jenis Kelamin");

        formLayout.add(gambarLabel, gambar, nama, email, nomorTelepon, jenisKelamin);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.dataadmin == null) {
                this.dataadmin = new Dataadmin();
            }
            this.dataadmin.setGambar(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Dataadmin value) {
        this.dataadmin = value;
        binder.readBean(this.dataadmin);
        this.gambarPreview.setVisible(value != null);
        if (value == null || value.getGambar() == null) {
            this.gambar.clearFileList();
            this.gambarPreview.setSrc("");
        } else {
            this.gambarPreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getGambar()));
        }

    }
}
