async function fetchDoctors() {
  const res = await fetch(`${API_BASE_URL}/doctors`);
  return res.json();
}
