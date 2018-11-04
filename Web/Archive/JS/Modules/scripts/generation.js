function setup() {
 defaultHeader();
}

function defaultHeader() {
  document.getElementById("header").innerHTML =
  "<a href=\"#default\" class=\"logo\"><img src=\"images/logo.png\" width=100></a>
  <div class=\"header-right\">
    <table>
      <tr>
        <td rowspan=\"2\"> <a href=\"#account\">Account</a> </td>
        <td rowspan=\"2\"> <a href=\"#logout\">Logout</a> </td>
      </tr>
    </table>
  </div> <!-- end of header-right -->";
}

function defaultContent() {

}
