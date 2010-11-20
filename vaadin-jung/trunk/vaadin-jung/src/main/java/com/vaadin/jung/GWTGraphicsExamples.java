package com.vaadin.jung;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.animation.Animate;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTGraphicsExamples implements EntryPoint, ChangeHandler,
		ValueChangeHandler<String> {

	private ListBox select;

	private DrawingArea canvas;

	private HorizontalPanel panel;

	private HTML sourceCode = new HTML() {
		public void setHTML(String html) {
			super.setHTML("<pre class=\"prettyprint\">" + html + "</pre>");
		};
	};

	public void onModuleLoad() {
		VerticalPanel mainPanel = new VerticalPanel();
		RootPanel.get().add(mainPanel);

		select = new ListBox();
		select.addChangeHandler(this);
		populateSelect();
		mainPanel.add(select);

		panel = new HorizontalPanel();
		mainPanel.add(this.panel);

		canvas = new DrawingArea(400, 400);
		canvas.setStyleName("drawing-area");
		mainPanel.add(canvas);

		DisclosurePanel sourceCodePanel = new DisclosurePanel("Source code");
		sourceCode.setStyleName("source-code");
		sourceCodePanel.setContent(sourceCode);
		sourceCodePanel.setAnimationEnabled(true);
		mainPanel.add(sourceCodePanel);

		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
	}

	private void populateSelect() {
		select.addItem("Select example", "");
		select.addItem("Circles", "circles");
		select.addItem("Path", "path");
		select.addItem("Mouse click", "mouseclick");
		select.addItem("Mouse move", "mousemove");
		select.addItem("Animation", "animation");

	}

	public void onChange(ChangeEvent event) {
		String value = select.getValue(select.getSelectedIndex());
		showExample(value);
		History.newItem(value, false);
	}

	private void showExample(String value) {
		canvas.clear();
		panel.clear();
		String code = "";
		/*-	
			"VerticalPanel panel = new VerticalPanel();\n"
				+ "RootPanel.get().add(panel);\n\n"
				+ "HorizontalPanel panel = new HorizontalPanel();\n"
				+ "mainPanel.add(panel);\n\n"
				+ "canvas = new DrawingArea(400, 400);\n"
				+ "canvas.setStyleName(\"drawing-area\");\n"
				+ "mainPanel.add(canvas);\n\n";
		 */
		if ("animation".equals(value)) {
			final Circle circle = new Circle(200, 200, 0);
			canvas.add(circle);

			Button animateButton = new Button("Animate circle");
			panel.add(animateButton);
			animateButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					new Animate(circle, "radius", 0, 100, 3000).start();
				}
			});

			code += "final Circle circle = new Circle(200, 200, 0);\n"
					+ "canvas.add(circle);\n\n"
					+ "Button animateButton = new Button(\"Animate circle\");\n"
					+ "panel.add(animateButton);\n"
					+ "animateButton.addClickHandler(new ClickHandler() {\n"
					+ "\tpublic void onClick(ClickEvent event) {\n"
					+ "\t\tnew Animate(circle, \"radius\", 0, 100, 3000).start();\n"
					+ "\t}\n" + "});";
		} else if ("mouseclick".equals(value)) {
			Rectangle rect = new Rectangle(10, 10, 100, 50);
			canvas.add(rect);
			rect.setFillColor("blue");
			rect.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Rectangle rect = (Rectangle) event.getSource();
					if (rect.getFillColor().equals("blue")) {
						rect.setFillColor("red");
					} else {
						rect.setFillColor("blue");
					}
				}
			});
			code += "Rectangle rect = new Rectangle(10, 10, 100, 50);\n"
					+ "canvas.add(rect);\n" + "rect.setFillColor(\"blue\");\n"
					+ "rect.addClickHandler(new ClickHandler() {\n"
					+ "\tpublic void onClick(ClickEvent event) {\n"
					+ "\t\tRectangle rect = (Rectangle) event.getSource();\n"
					+ "\t\tif (rect.getFillColor().equals(\"blue\")) {\n"
					+ "\t\t\trect.setFillColor(\"red\");\n" + "\t\t} else {\n"
					+ "\t\t\trect.setFillColor(\"blue\");\n"
					+ "\t\t}\n\t}\n});";
		} else if ("mousemove".equals(value)) {
			final Circle circle = new Circle(0, 0, 10);
			canvas.add(circle);
			canvas.addMouseMoveHandler(new MouseMoveHandler() {
				public void onMouseMove(MouseMoveEvent event) {
					circle.setX(event.getX());
					circle.setY(event.getY());
				}
			});
			code += "final Circle circle = new Circle(0, 0, 10);\n"
					+ "canvas.add(circle);\n"
					+ "canvas.addMouseMoveHandler(new MouseMoveHandler() {\n"
					+ "\tpublic void onMouseMove(MouseMoveEvent event) {\n"
					+ "\t\tcircle.setX(event.getX());\n"
					+ "\t\tcircle.setY(event.getY());\n" + "\t}\n});\n";
		} else if ("path".equals(value)) {
			final Path path = new Path(50, 50);
			path.lineRelativelyTo(100, 0);
			path.lineRelativelyTo(0, 100);
			path.lineRelativelyTo(-100, 0);
			path.close();
			canvas.add(path);

			final Button modifyButton = new Button("Modify path");
			panel.add(modifyButton);
			modifyButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (modifyButton.getHTML().equals("Modify path")) {
						path.setStep(2, new LineTo(true, -50, 100));
						path.removeStep(3);
						modifyButton.setHTML("Reset");
					} else {
						path.setStep(2, new LineTo(true, 0, 100));
						path.setStep(3, new LineTo(true, -100, 0));
						path.close();
						modifyButton.setHTML("Modify path");
					}
				}
			});
			code += "final Path path = new Path(50, 50);\n"
					+ "path.lineRelativelyTo(100, 0);\n"
					+ "path.lineRelativelyTo(0, 100);\n"
					+ "path.lineRelativelyTo(-100, 0);\n"
					+ "path.close();\n"
					+ "canvas.add(path);\n\n"
					+ "final Button modifyButton = new Button(\"Modify path\");\n"
					+ "panel.add(modifyButton);\n"
					+ "modifyButton.addClickHandler(new ClickHandler() {\n"
					+ "\tpublic void onClick(ClickEvent event) {\n"
					+ "\t\tif (modifyButton.getHTML().equals(\"Modify path\")) {\n"
					+ "\t\t\tpath.setStep(2, new LineTo(true, -50, 100));\n"
					+ "\t\t\tpath.removeStep(3);\n"
					+ "\t\t\tmodifyButton.setHTML(\"Reset\");\n"
					+ "\t\t} else {\n"
					+ "\t\t\tpath.setStep(2, new LineTo(true, 0, 100));\n"
					+ "\t\t\tpath.setStep(3, new LineTo(true, -100, 0));\n"
					+ "\t\t\tpath.close();\n"
					+ "\t\t\tmodifyButton.setHTML(\"Modify path\");\n"
					+ "\t\t}\n\t}\n});";
		} else if ("circles".equals(value)) {
			ClickHandler handler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					canvas.bringToFront((Circle) event.getSource());
				}
			};
			int xCoords[] = { 200, 225, 175 };
			int yCoords[] = { 150, 200, 200 };
			String fillColors[] = { "red", "blue", "green" };
			for (int i = 0; i < xCoords.length; i++) {
				Circle circle = new Circle(xCoords[i], yCoords[i], 50);
				circle.setFillColor(fillColors[i]);
				circle.setFillOpacity(0.5);
				circle.addClickHandler(handler);
				canvas.add(circle);
			}

			code += "ClickHandler handler = new ClickHandler() {\n"
					+ "\tpublic void onClick(ClickEvent event) {\n"
					+ "\t\tcanvas.pop((Circle) event.getSource());\n"
					+ "\t}\n};\n"
					+ "int xCoords[] = { 200, 225, 175 };\n"
					+ "int yCoords[] = { 150, 200, 200 };\n"
					+ "String fillColors[] = { \"red\", \"blue\", \"green\" };\n"
					+ "for (int i = 0; i < xCoords.length; i++) {\n"
					+ "\tCircle circle = new Circle(xCoords[i], yCoords[i], 50);\n"
					+ "\tcircle.setFillColor(fillColors[i]);\n"
					+ "\tcircle.setFillOpacity(0.5);\n"
					+ "\tcircle.addClickHandler(handler);\n"
					+ "\tcanvas.add(circle);\n}\n";
		}
		sourceCode.setHTML(code);
		prettyPrint();
	}

	private native void prettyPrint()
	/*-{
		$wnd.prettyPrint();
	}-*/;

	public void onValueChange(ValueChangeEvent<String> event) {
		String value = event.getValue();
		for (int i = 0; i < select.getItemCount(); i++) {
			if (select.getValue(i).equals(value)) {
				select.setSelectedIndex(i);
				showExample(value);
				break;
			}
		}
	}
}
