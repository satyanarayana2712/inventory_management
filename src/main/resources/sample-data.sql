USE inventory_management;

INSERT INTO users(username, password, role, email) VALUES
('admin', 'replace-with-salt-hash', 'ADMIN', 'admin@example.com'),
('manager', 'replace-with-salt-hash', 'INVENTORY_MANAGER', 'manager@example.com'),
('staff', 'replace-with-salt-hash', 'STAFF', 'staff@example.com')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO categories(category_name, description) VALUES
('Electronics', 'Electronic accessories and devices'),
('Office', 'Office inventory'),
('Warehouse', 'Warehouse supplies')
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO suppliers(company_name, phone, email, address) VALUES
('Northwind Supplies', '+1 555 0101', 'sales@northwind.example', '100 Market Street'),
('Acme Wholesale', '+1 555 0102', 'orders@acme.example', '200 Industrial Road'),
('Global Traders', '+1 555 0103', 'support@global.example', '300 Commerce Ave');

INSERT INTO products(name, description, category_id, supplier_id, purchase_price, selling_price, stock, minimum_stock, barcode) VALUES
('USB Keyboard', 'Full size USB keyboard', 1, 1, 18.50, 29.99, 25, 5, 'USBKEY1001'),
('Wireless Mouse', '2.4GHz wireless mouse', 1, 1, 9.75, 19.99, 4, 5, 'MOUSE1002'),
('Printer Paper', 'A4 500-sheet pack', 2, 2, 3.20, 5.50, 80, 20, 'PAPER1003')
ON DUPLICATE KEY UPDATE stock = VALUES(stock);
