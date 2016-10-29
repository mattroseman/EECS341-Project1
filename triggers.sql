CREATE TRIGGER total_price BEFORE INSERT ON orders
FOR EACH ROW
BEGIN
    IF NEW.dollars IS NULL THEN
        SET NEW.dollars = (SELECT price FROM products WHERE products.pid = NEW.pid) * NEW.qty;
    END IF;
END;
