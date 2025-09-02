function renderList(containerId, items, templateFn) {
  const container = document.getElementById(containerId);
  container.innerHTML = items.map(templateFn).join("");
}
