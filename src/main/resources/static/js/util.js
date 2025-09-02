function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString();
}

function showLoader() {
  document.body.insertAdjacentHTML("beforeend", `<div class="loader">Loading...</div>`);
}

function hideLoader() {
  const loader = document.querySelector(".loader");
  if (loader) loader.remove();
}
