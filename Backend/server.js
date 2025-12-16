const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3000;

// Load routes
const adminRoutes = require('./routes/admin');
app.use('/admin', adminRoutes);

app.get('/', (req, res) => res.send('SafeMail API running!'));

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
