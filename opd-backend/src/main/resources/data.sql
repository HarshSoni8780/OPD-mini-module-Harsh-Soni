-- Seed doctors
INSERT IGNORE INTO doctor (id, name, specialization) VALUES
    (1, 'Dr. Ananya Sharma', 'General Physician'),
    (2, 'Dr. Rajesh Patel', 'Pediatrician'),
    (3, 'Dr. Priya Verma', 'Orthopedic'),
    (4, 'Dr. Sanjay Gupta', 'ENT Specialist');

-- Seed sample patients for initial testing
INSERT IGNORE INTO patient (id, name, gender, age, phone, created_at) VALUES
    (1, 'Ravi Shah', 'MALE', 34, '9876543210', NOW()),
    (2, 'Meena Kumari', 'FEMALE', 28, '9123456780', NOW()),
    (3, 'Amit Joshi', 'MALE', 45, '9988776655', NOW());
