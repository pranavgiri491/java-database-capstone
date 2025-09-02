function showModal(message) {
  const modal = document.createElement("div");
  modal.classList.add("modal-overlay");
  modal.innerHTML = 
    <div class="modal">
      <p>${message}</p>
      <button onclick="closeModal()">Close</button>
    </div>
  ;
  document.body.appendChild(modal);
}

function closeModal() {
  const modal = document.querySelector(".modal-overlay");
  if (modal) modal.remove();
}
