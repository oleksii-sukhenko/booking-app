DELETE FROM payments;
DELETE FROM bookings;
DELETE FROM accommodations_amenities;
DELETE FROM accommodations;
DELETE FROM amenities;
DELETE FROM addresses;
DELETE FROM users_roles;
DELETE FROM users;
DELETE FROM roles;

INSERT INTO users (id, email, first_name, last_name, password)
VALUES (1, 'user@mail.com', 'username', 'user_lastname', '$2a$12$Q2ONP70Pf3mJ.N/WKrG0vOOOEdrDuvTm72FQu8IvpoI.mbgYbs9Da'),
(2, 'admin@mail.com', 'adminname', 'admin_lastname', '$2a$10$3T1ylV4VZQKymDDWfS2sFOMpGmO/31chYlY/rMIMbXtDglQKXeQde'),
(3, 'user2@mail.com', 'Wrong', 'User', '$2a$12$Q2ONP70Pf3mJ.N/WKrG0vOOOEdrDuvTm72FQu8IvpoI.mbgYbs9Da');

INSERT INTO roles (id, role)
VALUES (1, 'ROLE_MANAGER'), (2, 'ROLE_CUSTOMER');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2), (2, 1);

INSERT INTO addresses (id, country, city, street, number, postcode)
VALUES (1, 'Ukraine', 'Kyiv', 'Khreshchatyk', '1a', '01001');

INSERT INTO amenities (id, name)
VALUES (1, 'WiFi'), (2, 'TV');

INSERT INTO accommodations (id, type, size, daily_rate, availability, address_id, is_deleted)
VALUES
  (1, 'APARTMENT', 'Large', 100.0, 2, 1, false),
  (2, 'HOUSE', 'Medium', 80.0, 1, 1, false),
  (3, 'CONDO', 'Small', 50.0, 0, 1, false);

INSERT INTO accommodations_amenities (accommodation_id, amenity_id)
VALUES (1, 1), (1, 2), (2, 1), (3, 2);

INSERT INTO bookings (id, check_in_date, check_out_date, accommodation_id, user_id, status)
VALUES (1, '2025-06-25', '2025-06-26', 1, 1, 'CONFIRMED'),
(2, '2025-06-27', '2025-06-29', 2, 1, 'PENDING'),
(3, '2025-06-18', '2025-06-19', 2, 1, 'CONFIRMED');


INSERT INTO payments (id, status, booking_id, session_url, session_id, amount_to_pay, created_at)
VALUES
  (1, 'PENDING', 2, 'https://example.com/session1', 'session_abc123', 80.00, '2025-06-01 12:00:00'),
  (2, 'PAID', 1, 'https://example.com/session2', 'session_def456', 100.00, '2025-06-02 15:30:00'),
  (3, 'PENDING', 3, 'https://example.com/session3', 'session_ghi789', 50.00, '2025-05-30 09:45:00');