document.addEventListener("DOMContentLoaded", async () => {
  const doctors = await fetchDoctors();
  const container = document.querySelector(".card-grid");
  container.innerHTML = doctors.map(doctorCard).join("");
});
