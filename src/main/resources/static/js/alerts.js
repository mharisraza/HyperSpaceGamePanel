$(document).ready(function() {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": true,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
        }
      var status = document.getElementById("status").value;
    // check status and based on status give specific toast.

    if(status == "USER_REGISTERED_SUCCESSFULLY") {
        toastr["success"]("Register Successfully!", "Success");
    }

    if(status == "USER_ALREADY_EXISTS_WITH_PROVIDED_EMAIL") {
        toastr["error"]("User with provided email address already exist, try another.", "Error");
    }
});
