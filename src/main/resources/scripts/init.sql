CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name TEXT
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name TEXT,
    price INT
);

CREATE TABLE inventory (
    product_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL CHECK (quantity >= 0),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,
    pending INT NOT NULL CHECK (pending = 0 OR pending = 1),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE order_details (
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO customers VALUES (12, 'John Smith');
INSERT INTO customers VALUES (33, 'Franklin Alder');
INSERT INTO customers VALUES (37, 'Lynda Sheldon');
INSERT INTO customers VALUES (41, 'Logan Michael');
INSERT INTO customers VALUES (67, 'Lorena Clyde');
INSERT INTO customers VALUES (88, 'Peter Styles');
INSERT INTO customers VALUES (102, 'Rupert Gordon');

INSERT INTO products VALUES (2, 'computer', 1234);
INSERT INTO products VALUES (4, 'sun glasses', 12);
INSERT INTO products VALUES (6, 'toothbrush', 2);
INSERT INTO products VALUES (9, 'piano', 2600);
INSERT INTO products VALUES (15, 'tissue box', 4);
INSERT INTO products VALUES (22, 'drill press', 140);
INSERT INTO products VALUES (25, 'slippers', 9);
INSERT INTO products VALUES (31, 'light bulb', 2);
INSERT INTO products VALUES (32, 'a4 paper', 2);
INSERT INTO products VALUES (38, 'flowers', 15);
INSERT INTO products VALUES (43, 'bracelet', 3);
INSERT INTO products VALUES (45, 'conditioner', 5);
INSERT INTO products VALUES (57, 'sailboat', 25000);
INSERT INTO products VALUES (61, 'plant', 7);
INSERT INTO products VALUES (65, 'pen', 1);
INSERT INTO products VALUES (77, 'shoelace', 1);
INSERT INTO products VALUES (87, 'bed', 100);
INSERT INTO products VALUES (89, 'boom box', 59);
INSERT INTO products VALUES (99, 'mobile phone', 159);
INSERT INTO products VALUES (100, 'rubber duck', 4);
INSERT INTO products VALUES (112, 'teddy bear', 10);

INSERT INTO inventory VALUES (2, 2);
INSERT INTO inventory VALUES (4, 10);
INSERT INTO inventory VALUES (6, 100);
INSERT INTO inventory VALUES (9, 1);
INSERT INTO inventory VALUES (15, 49);
INSERT INTO inventory VALUES (25, 33);
INSERT INTO inventory VALUES (31, 999);
INSERT INTO inventory VALUES (32, 10010);
INSERT INTO inventory VALUES (38, 34);
INSERT INTO inventory VALUES (61, 45);
INSERT INTO inventory VALUES (65, 234);
INSERT INTO inventory VALUES (77, 1234);
INSERT INTO inventory VALUES (87, 5);
INSERT INTO inventory VALUES (89, 22);
INSERT INTO inventory VALUES (99, 124);
INSERT INTO inventory VALUES (100, 224);
INSERT INTO inventory VALUES (112, 47);

INSERT INTO orders VALUES (2, 12, '2019-01-05', 0);
INSERT INTO orders VALUES (4, 33, '2019-01-11', 0);
INSERT INTO orders VALUES (5, 33, '2019-01-11', 0);
INSERT INTO orders VALUES (6, 33, '2019-01-12', 0);
INSERT INTO orders VALUES (11, 67, '2019-02-01', 0);
INSERT INTO orders VALUES (12, 41, '2019-02-02', 1);
INSERT INTO orders VALUES (19, 12, '2019-02-02', 1);
INSERT INTO orders VALUES (36, 102, '2019-02-02', 0);
INSERT INTO orders VALUES (37, 41, '2019-02-05', 1);

INSERT INTO order_details VALUES (2, 4, 2);
INSERT INTO order_details VALUES (2, 6, 1);
INSERT INTO order_details VALUES (2, 32, 100);
INSERT INTO order_details VALUES (4, 2, 1);
INSERT INTO order_details VALUES (4, 87, 1);
INSERT INTO order_details VALUES (5, 38, 3);
INSERT INTO order_details VALUES (6, 112, 1);
INSERT INTO order_details VALUES (11, 99, 1);
INSERT INTO order_details VALUES (11, 31, 3);
INSERT INTO order_details VALUES (12, 25, 1);
INSERT INTO order_details VALUES (19, 87, 1);
INSERT INTO order_details VALUES (36, 87, 1);
INSERT INTO order_details VALUES (37, 45, 3);
INSERT INTO order_details VALUES (37, 77, 2);
