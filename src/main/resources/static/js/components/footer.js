function renderFooter() {
  document.getElementById("footer").innerHTML = 
    <footer class="site-footer">
      <div class="container">
        <p>&copy; ${new Date().getFullYear()} Hospital Management System. All rights reserved.</p>
      </div>
    </footer>
  ;
}
