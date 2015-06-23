package com.wheany;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.io.File;
import java.io.IOException;


/**
 *
 */
@Theme("mytheme")
@Widgetset("com.wheany.MyAppWidgetset")
public class MyUI extends UI {

    private String getFilename() {
        return "generatedImageTest-" + System.currentTimeMillis() + ".png";
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        // Find the application directory
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

        // Image as a file resource
        FileResource fileResource = new FileResource(new File(basepath + "/test_image.jpg"));

        // Show the image in the application
        Image fileImage = new Image("Original image", fileResource);

        layout.addComponent(fileImage);

        // Create an instance of our stream source.
        final ImageResource imageResource;
        try {
            imageResource = new ImageResource(new File(basepath + "/test_image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final StreamResource resource = new StreamResource(imageResource, getFilename());
        final Image image = new Image("Dynamic image", resource);
        layout.addComponent(image);

        Button button = new Button("Reload image");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                image.markAsDirty();
                resource.setFilename(getFilename());
            }
        });
        layout.addComponent(button);

        final Slider slider = new Slider(1, 100);
        slider.addValueChangeListener(
            new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent event) {
                    double value = slider.getValue();

                    imageResource.setValue(value);
                    // Use the value
                    image.markAsDirty();
                    resource.setFilename(getFilename());
                }
            }
        );
        layout.addComponent(slider);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
