package web.util;

import structure.Product;

import java.util.List;

public class HTMLBuilder {

    private final StringBuilder html;

    public HTMLBuilder() {
        this.html = new StringBuilder();
        startHTML();
    }

    private void startHTML() {
        html.append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                .append("<title></title>")
                .append("<style>")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid black; padding: 8px; text-align: left; }")
                .append("</style>")
                .append("</head>")
                .append("<body>");
    }

    public HTMLBuilder setTitle(String title) {
        html.append("<title>").append(title).append("</title>");
        return this;
    }

    public HTMLBuilder addHeader(String header) {
        html.append("<h1>").append(header).append("</h1>");
        return this;
    }

    public HTMLBuilder startTable(String[] columnHeaders) {
        html.append("<table>")
                .append("<tr>");
        for (String header : columnHeaders) {
            html.append("<th>").append(header).append("</th>");
        }
        html.append("</tr>");
        return this;
    }

    public HTMLBuilder addProductsToTable(List<Product> products) {
        for (Product product : products) {
            html.append("<tr>")
                    .append("<td>").append(product.getProductID()).append("</td>")
                    .append("<td>").append(product.getName()).append("</td>")
                    .append("<td>").append(product.getCategory()).append("</td>")
                    .append("<td>").append(product.getPrice()).append("</td>")
                    .append("<td>").append(product.getQuantity()).append("</td>")
                    .append("<td>").append(product.getDescription()).append("</td>")
                    .append("</tr>");
        }
        return this;
    }

    public HTMLBuilder endTable() {
        html.append("</table>");
        return this;
    }

    public String build() {
        html.append("</body>")
                .append("</html>");
        return html.toString();
    }
}
