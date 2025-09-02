async function fetchAppointments() {
  const res = await fetch(`${API_BASE_URL}/appointments`);
  return res.json();
}

async function updateAppointment(id, data) {
  const res = await fetch(`${API_BASE_URL}/appointments/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return res.json();
}
