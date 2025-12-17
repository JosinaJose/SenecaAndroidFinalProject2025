const express = require('express');
const router = express.Router();
const pool = require('../db'); // Use your existing connection pool
const bcrypt = require('bcrypt');

/**
 * CREATE ADMIN
 * POST /admin/create
 */
router.post('/create', async (req, res) => {
  const { companyName, firstName, lastName, phoneNumber, email, password } = req.body;

  try {
    // Hash the password before saving to db
    const hashedPassword = await bcrypt.hash(password, 10);

    const [result] = await pool.query(
      `INSERT INTO admins 
       (companyName, firstName, lastName, phoneNumber, email, password)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [companyName, firstName, lastName, phoneNumber, email, hashedPassword]
    );

    const [rows] = await pool.query(
      'SELECT * FROM admins WHERE id = ?',
      [result.insertId]
    );

    res.status(201).json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
});

/**
 * LOGIN ADMIN
 * POST /admin/login
 */
router.post('/login', async (req, res) => {
  const { email, password } = req.body;

  try {
    const [rows] = await pool.query(
      'SELECT * FROM admins WHERE email = ?',
      [email]
    );

    if (rows.length === 0) {
      return res.status(404).json({ error: 'Admin not found' });
    }

    const admin = rows[0];

    // Compare password with hashed password
    const match = await bcrypt.compare(password, admin.password);
    if (!match) {
      return res.status(401).json({ error: 'Invalid password' });
    }

    // Remove password before sending response
    delete admin.password;
    res.json(admin);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
});

/**
 * UPDATE ADMIN INFO
 * PUT /admin/updateAdmin
 * Body: { id, firstName, lastName, email, phoneNumber, companyName? }
 */
router.put('/updateAdmin', async (req, res) => {
    const { id, firstName, lastName, email, phoneNumber, companyName } = req.body;

    console.log('Received update request:', req.body);

    // Validation
    if (!id || !firstName || !lastName || !email || !phoneNumber) {
        return res.status(400).json({
            success: false,
            message: 'All fields are required',
            receivedData: { id, firstName, lastName, email, phoneNumber }
        });
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        return res.status(400).json({
            success: false,
            message: 'Invalid email format'
        });
    }

    try {
        // Check if admin exists first
        const [adminCheck] = await pool.query(
            'SELECT id FROM admins WHERE id = ?',
            [id]
        );

        if (adminCheck.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Admin not found',
                requestedId: id
            });
        }

        // Check if email is already used by another admin
        const [existingAdmin] = await pool.query(
            'SELECT id FROM admins WHERE email = ? AND id != ?',
            [email, id]
        );

        if (existingAdmin.length > 0) {
            return res.status(409).json({
                success: false,
                message: 'Email already in use by another admin'
            });
        }

        // Update admin info - include companyName if provided
        let updateQuery, updateParams;
        
        if (companyName !== undefined && companyName !== null) {
            updateQuery = 'UPDATE admins SET firstName = ?, lastName = ?, email = ?, phoneNumber = ?, companyName = ? WHERE id = ?';
            updateParams = [firstName, lastName, email, phoneNumber, companyName, id];
        } else {
            updateQuery = 'UPDATE admins SET firstName = ?, lastName = ?, email = ?, phoneNumber = ? WHERE id = ?';
            updateParams = [firstName, lastName, email, phoneNumber, id];
        }

        const [result] = await pool.query(updateQuery, updateParams);

        console.log('Update result:', result);

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: 'No rows were updated'
            });
        }

        // Fetch the updated admin to return
        const [updatedRows] = await pool.query(
            'SELECT id, companyName, firstName, lastName, email, phoneNumber FROM admins WHERE id = ?',
            [id]
        );

        res.status(200).json({
            success: true,
            message: 'Admin information updated successfully',
            data: updatedRows[0]
        });

    } catch (error) {
        console.error('Error updating admin:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
});

/**
 * GET ADMIN BY ID
 * GET /admin/:id
 */
router.get('/:id', async (req, res) => {
    const { id } = req.params;

    try {
        const [rows] = await pool.query(
            'SELECT id, companyName, firstName, lastName, email, phoneNumber FROM admins WHERE id = ?',
            [id]
        );

        if (rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Admin not found'
            });
        }

        res.status(200).json({
            success: true,
            data: rows[0]
        });

    } catch (error) {
        console.error('Error fetching admin:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
});

/**
 * GET ADMIN BY EMAIL
 * GET /admin/email/:email
 */
router.get('/email/:email', async (req, res) => {
  try {
    const { email } = req.params;

    const [rows] = await pool.query(
      'SELECT * FROM admins WHERE email = ?',
      [email]
    );

    if (rows.length === 0) {
      return res.status(404).json({ error: 'Admin not found' });
    }

    const admin = { ...rows[0] };
    delete admin.password;

    res.json(admin);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
});

module.exports = router;