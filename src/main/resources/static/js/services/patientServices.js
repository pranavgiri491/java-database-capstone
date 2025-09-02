async function fetchPatients() {
  const res = await fetch(`${API_BASE_URL}/patients`);
  return res.json();
}

async function fetchPatientRecords(id) {
  const res = await fetch(`${API_BASE_URL}/patients/${id}/records`);
  return res.json();
}
