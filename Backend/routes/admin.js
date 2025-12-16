

const express = require('express');
const router = express.Router();
const pool = require('../db');
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
 * GET ADMIN BY EMAIL
 * GET /admin/email/:email
 * Used for login verification
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

    // For security, do not return the password in the response
    const admin = { ...rows[0] };
    delete admin.password;

    res.json(admin);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
});

/**
 * LOGIN ADMIN
 * POST /admin/login
 * Body: { email, password }
 * Checks email and password
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
 * GET ADMIN BY ID
 * GET /admin/:id
 */
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const [rows] = await pool.query(
      'SELECT * FROM admins WHERE id = ?',
      [id]
    );

    if (rows.length === 0) {
      return res.status(404).json({ error: 'Admin not found' });
    }

    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Database error' });
  }
});

module.exports = router;
