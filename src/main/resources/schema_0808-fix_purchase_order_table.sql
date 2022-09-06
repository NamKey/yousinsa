SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sample`;
CREATE TABLE `sample`
(
    `id`      INTEGER AUTO_INCREMENT NOT NULL,
    `comment` TEXT,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`            bigint PRIMARY KEY AUTO_INCREMENT,
    `user_name`     varchar(255),
    `user_email`    varchar(255),
    `user_password` varchar(255),
    `user_role`     ENUM ('BUYER', 'STORE_OWNER', 'ADMIN'),
    `created_at`    timestamp,
    `updated_at`    timestamp
);

DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores`
(
    `id`           bigint PRIMARY KEY AUTO_INCREMENT,
    `store_name`   varchar(255),
    `store_owner`  bigint,
    `store_status` ENUM ('REQUESTED', 'ACCEPTED', 'REJECTED', 'PENDING'),
    `created_at`   timestamp,
    `updated_at`   timestamp
);

DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `store_id`   bigint,
    `category`   ENUM ('TOP', 'OUTER', 'PANTS'),
    `price`      int,
    `created_at` timestamp,
    `updated_at` timestamp
);

DROP TABLE IF EXISTS `product_options`;
CREATE TABLE `product_options`
(
    `id`            bigint PRIMARY KEY AUTO_INCREMENT,
    `product_id`    bigint,
    `product_count` int,
    `product_size`  varchar(255)
);

DROP TABLE IF EXISTS `purchase_orders`;
CREATE TABLE `purchase_orders`
(
    `id`                    bigint PRIMARY KEY AUTO_INCREMENT,
    `buyer_id`              bigint,
    `purchase_order_status` ENUM ('PAID', 'ACCEPTED', 'CANCELLED'),
    `created_at`            timestamp,
    `updated_at`            timestamp
);

DROP TABLE IF EXISTS `purchase_order_items`;
CREATE TABLE `purchase_order_items`
(
    `id`                    bigint PRIMARY KEY AUTO_INCREMENT,
    `purchase_order_id`     bigint,
    `product_option_id`     bigint,
    `purchase_order_amount` int
);

ALTER TABLE `stores`
    ADD FOREIGN KEY (`store_owner`) REFERENCES `users` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `products`
    ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `purchase_orders`
    ADD FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `purchase_order_items`
    ADD FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `purchase_order_items`
    ADD FOREIGN KEY (`product_option_id`) REFERENCES `product_options` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

DROP TABLE IF EXISTS `events`;
CREATE TABLE `events`
(
    `id`            bigint PRIMARY KEY AUTO_INCREMENT,
    `event_type`    varchar(255),
    `discount_rate` float,
    `started_at`    timestamp,
    `ended_at`      timestamp,
    `created_at`    timestamp,
    `updated_at`    timestamp
);

DROP TABLE IF EXISTS `event_products`;
CREATE TABLE `event_products`
(
    `id`         bigint PRIMARY KEY AUTO_INCREMENT,
    `product_id` bigint,
    `event_id`   bigint
);

ALTER TABLE `event_products`
    ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `event_products`
    ADD FOREIGN KEY (`event_id`) REFERENCES `events` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE;


SET FOREIGN_KEY_CHECKS = 1;
