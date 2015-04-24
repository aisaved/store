INSERT INTO item_attribute(
            attribute_name, attribute_type)
    VALUES ('color', 'char');

INSERT INTO item_attribute(
            attribute_name, attribute_type)
    VALUES ('size', 'integer');


INSERT INTO attribute_char(
            attribute_char_value, item_attribute_id, item_id)
    VALUES ('red', 1, 1);

INSERT INTO attribute_integer(
            attribute_integer_value, item_attribute_id, item_id)
    VALUES (10, 1, 1);


select item.item_id, item.item_code_sku, item_attribute.attribute_name, attribute_char.attribute_char_value,
attribute_integer.attribute_integer_value
from item 
inner join attribute_char on item.item_id = attribute_char.item_id
inner join attribute_integer on item.item_id = attribute_integer.item_id
inner join item_attribute on attribute_char.item_attribute_id = item_attribute.item_attribute_id
where (item_attribute.attribute_name = 'color' or item_attribute.attribute_name = 'size')
and attribute_char.attribute_char_value = 'red'
and attribute_integer.attribute_integer_value = 10



INSERT INTO item_attribute_value(
             attribute_type, attribute_char_value, 
            attribute_integer_value, item_attribute_id, item_id)
    VALUES ( 'char', 'red', 
            0, 1, 1);

            INSERT INTO item_attribute_value(
             attribute_type, attribute_char_value, 
            attribute_integer_value, item_attribute_id, item_id)
    VALUES ( 'integer', '', 
            10, 2, 1);


select item.item_id, item.item_code_sku,
item_attribute.attribute_name,
item_attribute_value.attribute_char_value, item_attribute_value.attribute_integer_value,
inner join item_attribute_value on item.item_id = item_attribute_value.item_id
inner join item_attribute attribute_char.item_attribute_id = item_attribute.item_attribute_id


/*This is for testing */
CREATE TABLE stock_transaction_test(
       transaction_id serial PRIMARY KEY,
       stock INTEGER NOT NULL default 0,
       name VARCHAR (255)
);
