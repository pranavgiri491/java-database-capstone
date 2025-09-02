async function addPrescription(data) {
  const res = await fetch(`${API_BASE_URL}/prescriptions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return res.json();
}
