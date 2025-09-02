function renderHeader() {
  document.getElementById("header").innerHTML = 
    <header class="site-header">
      <div class="container flex-between">
        <img src="src/main/resources/static/assets/images/logo/logo.png" alt="Logo" class="logo-sm"></img>
        <nav>
          <ul class="nav">
            <li><a href="/index.html">Home</a></li>
            <li><a href="/pages/patientDashboard.html">Patient</a></li>
            <li><a href="/templates/doctor/doctorDashboard.html">Doctor</a></li>
            <li><a href="/templates/admin/adminDashboard.html">Admin</a></li>
          </ul>
        </nav>
      </div>
    </header>
  ;
}
