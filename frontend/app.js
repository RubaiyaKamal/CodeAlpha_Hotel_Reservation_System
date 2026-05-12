'use strict';

// ─── Seed Data ────────────────────────────────────────────────────────────────

// Unsplash CDN helper — width/height cropped, auto format, 80% quality
const UNS = id =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=800&h=480&q=80`;

const SEED_ROOMS = [
  { number: 101, type: 'Standard', price: 80,  available: true,
    desc: 'Queen bed with garden view',
    amenities: ['Queen Bed', 'Garden View', 'Wi-Fi', 'AC'],
    img: UNS('photo-1631049307264-da0ec9d70304') },
  { number: 102, type: 'Standard', price: 80,  available: true,
    desc: 'Twin beds near the pool',
    amenities: ['Twin Beds', 'Pool Access', 'Wi-Fi', 'AC'],
    img: UNS('photo-1566665797739-1674de7a421a') },
  { number: 103, type: 'Standard', price: 85,  available: true,
    desc: 'Corner room with queen bed and city view',
    amenities: ['Queen Bed', 'City View', 'Wi-Fi', 'AC'],
    img: UNS('photo-1618773928121-c32242e63f39') },
  { number: 201, type: 'Deluxe',   price: 150, available: true,
    desc: 'King bed with city panorama and mini-bar',
    amenities: ['King Bed', 'Mini-Bar', 'City View', 'Balcony'],
    img: UNS('photo-1611892440504-42a792e24d32') },
  { number: 202, type: 'Deluxe',   price: 155, available: true,
    desc: 'King bed with pool view and balcony',
    amenities: ['King Bed', 'Pool View', 'Balcony', 'Bathtub'],
    img: UNS('photo-1590490360182-c33d57733427') },
  { number: 203, type: 'Deluxe',   price: 160, available: true,
    desc: 'Corner room with balcony and sea view',
    amenities: ['King Bed', 'Sea View', 'Balcony', 'Lounge'],
    img: UNS('photo-1578683010236-d716f9a3f461') },
  { number: 301, type: 'Suite',    price: 300, available: true,
    desc: 'Luxury suite with living area and jacuzzi',
    amenities: ['King Bed', 'Living Area', 'Jacuzzi', 'Butler'],
    img: UNS('photo-1582719478250-c89cae4dc85b') },
  { number: 302, type: 'Suite',    price: 350, available: true,
    desc: 'Presidential suite with private terrace and butler',
    amenities: ['King Bed', 'Private Terrace', 'Butler', 'Jacuzzi'],
    img: UNS('photo-1571003123894-1f0594d2b5d9') },
];

const ROOMS_VER = 2;   // bump when SEED_ROOMS structure changes
const KEYS = { rooms: 'gh_rooms', reservations: 'gh_reservations', ver: 'gh_rooms_ver' };

// ─── Storage helpers ──────────────────────────────────────────────────────────

function getRooms()        { return JSON.parse(localStorage.getItem(KEYS.rooms)); }
function getReservations() {
  try { return JSON.parse(localStorage.getItem(KEYS.reservations)) || []; }
  catch { return []; }
}
function saveRooms(r)      { localStorage.setItem(KEYS.rooms, JSON.stringify(r)); }
function saveReservations(r){ localStorage.setItem(KEYS.reservations, JSON.stringify(r)); }

function initData() {
  // Re-seed rooms whenever ROOMS_VER bumps (picks up new fields like img)
  if (localStorage.getItem(KEYS.ver) !== String(ROOMS_VER)) {
    saveRooms(SEED_ROOMS);
    localStorage.setItem(KEYS.ver, String(ROOMS_VER));
  }
  if (!localStorage.getItem(KEYS.reservations)) saveReservations([]);
}

// ─── ID / util helpers ────────────────────────────────────────────────────────

function genId(prefix) {
  return prefix + '-' + Math.random().toString(36).substr(2, 6).toUpperCase();
}

function nightsBetween(checkIn, checkOut) {
  return Math.round((new Date(checkOut) - new Date(checkIn)) / 86400000);
}

function formatDate(dateStr) {
  return new Date(dateStr + 'T00:00:00').toLocaleDateString('en-US',
    { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' });
}

function today() { return new Date().toISOString().split('T')[0]; }

// ─── Toast notifications ──────────────────────────────────────────────────────

function toast(msg, type = 'info') {
  const container = document.getElementById('toast-container');
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  const icons = { success: '✓', error: '✕', info: 'ℹ' };
  el.innerHTML = `<span>${icons[type] || 'ℹ'}</span><span>${msg}</span>`;
  container.appendChild(el);
  setTimeout(() => { el.style.opacity = '0'; el.style.transform = 'translateX(120%)';
    el.style.transition = 'all 0.3s'; setTimeout(() => el.remove(), 300); }, 3500);
}

// ─── Room rendering ───────────────────────────────────────────────────────────

let activeFilter = 'all';

function renderRooms(filter) {
  if (filter !== undefined) activeFilter = filter;
  const rooms = getRooms();
  const grid  = document.getElementById('room-grid');

  const filtered = rooms.filter(r =>
    r.available && (activeFilter === 'all' || r.type.toLowerCase() === activeFilter)
  );

  // Update filter button states
  document.querySelectorAll('.filter-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.filter === activeFilter);
  });

  if (filtered.length === 0) {
    grid.innerHTML = '<p class="no-rooms">No available rooms in this category.</p>';
    return;
  }

  const fallback = t => `this.onerror=null;this.parentElement.classList.add('room-img-${t.toLowerCase()}');this.style.display='none'`;

  grid.innerHTML = filtered.map(room => `
    <div class="room-card">
      <div class="room-image">
        <img
          src="${room.img || ''}"
          alt="Room ${room.number} — ${room.type}"
          class="room-photo"
          loading="lazy"
          onerror="${fallback(room.type)}"
        />
        <div class="room-image-overlay"></div>
        <span class="room-badge ${room.type.toLowerCase()}">${room.type}</span>
        <div class="room-number">Room ${room.number}</div>
      </div>
      <div class="room-info">
        <h3>${room.desc}</h3>
        <div class="room-amenities">
          ${room.amenities.map(a => `<span class="amenity-tag">${a}</span>`).join('')}
        </div>
        <div class="room-footer">
          <div class="room-price">$${room.price}<small>/night</small></div>
          <button class="btn btn-primary" onclick="openBookingModal(${room.number})">
            Book Now
          </button>
        </div>
      </div>
    </div>
  `).join('');
}

// ─── Booking Modal ────────────────────────────────────────────────────────────

let selectedRoom = null;

function openBookingModal(roomNumber) {
  const rooms = getRooms();
  selectedRoom = rooms.find(r => r.number === roomNumber);
  if (!selectedRoom || !selectedRoom.available) {
    toast('This room is no longer available.', 'error'); return;
  }

  document.getElementById('strip-name').textContent =
    `Room ${selectedRoom.number} — ${selectedRoom.type}`;
  document.getElementById('strip-price').textContent =
    `$${selectedRoom.price}/night`;

  const t = today();
  document.getElementById('check-in').min  = t;
  document.getElementById('check-out').min = t;
  document.getElementById('booking-form').reset();
  document.getElementById('price-summary').style.display = 'none';

  openOverlay('booking-overlay');
}

function updatePriceSummary() {
  if (!selectedRoom) return;
  const ci = document.getElementById('check-in').value;
  const co = document.getElementById('check-out').value;
  if (!ci || !co) return;

  const nights = nightsBetween(ci, co);
  if (nights <= 0) {
    document.getElementById('price-summary').style.display = 'none'; return;
  }

  document.getElementById('sum-room').textContent =
    `Room ${selectedRoom.number} × ${nights} night${nights !== 1 ? 's' : ''}`;
  document.getElementById('sum-rate').textContent = `$${selectedRoom.price} × ${nights}`;
  document.getElementById('sum-total').textContent = `$${(nights * selectedRoom.price).toFixed(2)}`;
  document.getElementById('price-summary').style.display = 'block';
}

async function handleBookingSubmit(e) {
  e.preventDefault();

  const name    = document.getElementById('guest-name').value.trim();
  const email   = document.getElementById('guest-email').value.trim();
  const phone   = document.getElementById('guest-phone').value.trim();
  const checkIn = document.getElementById('check-in').value;
  const checkOut= document.getElementById('check-out').value;
  const payment = document.getElementById('payment-method').value;

  if (nightsBetween(checkIn, checkOut) <= 0) {
    toast('Check-out must be after check-in.', 'error'); return;
  }

  const nights = nightsBetween(checkIn, checkOut);
  const total  = nights * selectedRoom.price;
  const btn    = document.getElementById('submit-btn');

  btn.disabled = true;
  btn.textContent = 'Processing payment…';

  await new Promise(r => setTimeout(r, 1400));

  const reservation = {
    id:            genId('RES'),
    customerId:    genId('CUST'),
    name, email, phone,
    roomNumber:    selectedRoom.number,
    roomType:      selectedRoom.type,
    roomDesc:      selectedRoom.desc,
    checkIn, checkOut, nights,
    pricePerNight: selectedRoom.price,
    total,
    paymentMethod: payment,
    transactionId: genId('TXN'),
    status:        'CONFIRMED',
    bookedAt:      new Date().toISOString(),
  };

  // Persist reservation
  const reservations = getReservations();
  reservations.push(reservation);
  saveReservations(reservations);

  // Mark room occupied — wrapped so a missing room can never block the confirmation
  try {
    const rooms = getRooms();
    const room  = rooms.find(r => r.number === selectedRoom.number);
    if (room) { room.available = false; saveRooms(rooms); }
  } catch (err) {
    console.warn('room availability update failed:', err);
  }

  closeOverlay('booking-overlay');
  btn.disabled = false;
  btn.textContent = 'Confirm Booking';

  renderRooms();
  showConfirmation(reservation);

  // Pre-fill the reservations search box so the user can find their booking instantly
  document.getElementById('search-email').value = email;
}

// ─── Confirmation Modal ───────────────────────────────────────────────────────

function showConfirmation(res) {
  const set = (id, val) => { const el = document.getElementById(id); if(el) el.textContent = val; };
  set('conf-id',      res.id);
  set('conf-name',    res.name);
  set('conf-email',   res.email);
  set('conf-phone',   res.phone);
  set('conf-room',    `${res.roomNumber} (${res.roomType})`);
  set('conf-desc',    res.roomDesc);
  set('conf-checkin', formatDate(res.checkIn));
  set('conf-checkout',formatDate(res.checkOut));
  set('conf-nights',  res.nights + ' night' + (res.nights !== 1 ? 's' : ''));
  set('conf-rate',    `$${res.pricePerNight}/night`);
  set('conf-payment', res.paymentMethod);
  set('conf-txn',     res.transactionId);
  set('conf-total',   `$${res.total.toFixed(2)}`);
  openOverlay('confirm-overlay');
}

// ─── My Reservations ──────────────────────────────────────────────────────────

function searchReservations() {
  const email = document.getElementById('search-email').value.trim().toLowerCase();
  if (!email) { toast('Enter your email address.', 'error'); return; }

  const list = getReservations().filter(r => r.email.toLowerCase() === email);
  const grid = document.getElementById('reservations-grid');

  if (list.length === 0) {
    const total = getReservations().length;
    const hint  = total === 0
      ? 'No bookings have been made through this page yet. Book a room first!'
      : `There are ${total} booking(s) stored — check the email matches exactly what you used when booking.`;
    grid.innerHTML = `
      <div class="no-results">
        <p>No reservations found for <strong>${email}</strong>.</p>
        <p style="margin-top:0.5rem;font-size:0.8rem;opacity:0.7">${hint}</p>
      </div>`;
    return;
  }

  grid.innerHTML = list.map(r => `
    <div class="res-card">
      <div class="res-card-header">
        <span class="res-id">${r.id}</span>
        <span class="res-status ${r.status.toLowerCase()}">${r.status}</span>
      </div>
      <div class="res-row"><span class="res-label">Room</span>
        <span class="res-value">${r.roomNumber} — ${r.roomType}</span></div>
      <div class="res-row"><span class="res-label">Check-In</span>
        <span class="res-value">${formatDate(r.checkIn)}</span></div>
      <div class="res-row"><span class="res-label">Check-Out</span>
        <span class="res-value">${formatDate(r.checkOut)}</span></div>
      <div class="res-row"><span class="res-label">Nights</span>
        <span class="res-value">${r.nights}</span></div>
      <div class="res-row"><span class="res-label">Payment</span>
        <span class="res-value">${r.paymentMethod}</span></div>
      <div class="res-row"><span class="res-label">Total</span>
        <span class="res-value res-total">$${r.total.toFixed(2)}</span></div>
    </div>
  `).join('');
}

// ─── Cancel Reservation ───────────────────────────────────────────────────────

function calculateRefund(reservation) {
  const daysUntil = nightsBetween(today(), reservation.checkIn);
  if (daysUntil >= 7) return { pct: 100, amount: reservation.total };
  if (daysUntil >= 3) return { pct: 50,  amount: reservation.total * 0.5 };
  return { pct: 0, amount: 0 };
}

function handleCancellation() {
  const id  = document.getElementById('cancel-id').value.trim().toUpperCase();
  if (!id) { toast('Enter a Reservation ID.', 'error'); return; }

  const reservations = getReservations();
  const idx = reservations.findIndex(r => r.id === id && r.status === 'CONFIRMED');

  if (idx === -1) {
    toast('Reservation not found or already cancelled.', 'error'); return;
  }

  const res    = reservations[idx];
  const refund = calculateRefund(res);
  const msg    = refund.amount > 0
    ? `${refund.pct}% refund of $${refund.amount.toFixed(2)} will be returned to your payment method.`
    : 'No refund (cancellation within 3 days of check-in).';

  if (!confirm(`Cancel reservation ${id}?\n\n${msg}`)) return;

  reservations[idx].status = 'CANCELLED';
  saveReservations(reservations);

  const rooms = getRooms();
  const room  = rooms.find(r => r.number === res.roomNumber);
  if (room) { room.available = true; saveRooms(rooms); }

  toast(`Reservation ${id} cancelled. ${msg}`, 'success');
  renderRooms();
  document.getElementById('cancel-id').value = '';

  // Refresh reservations list if currently showing this email
  const searchEmail = document.getElementById('search-email').value.trim();
  if (searchEmail) searchReservations();
}

// ─── Modal helpers ────────────────────────────────────────────────────────────

function openOverlay(id)  { document.getElementById(id).classList.add('active'); }
function closeOverlay(id) { document.getElementById(id).classList.remove('active'); }

// Close on backdrop click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('active');
  }
});

// ─── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  initData();
  renderRooms('all');

  document.getElementById('booking-form')
    .addEventListener('submit', handleBookingSubmit);

  document.getElementById('check-in')
    .addEventListener('change', updatePriceSummary);
  document.getElementById('check-out')
    .addEventListener('change', updatePriceSummary);

  // Allow pressing Enter in the reservations search bar
  document.getElementById('search-email')
    .addEventListener('keydown', e => { if (e.key === 'Enter') searchReservations(); });
});
