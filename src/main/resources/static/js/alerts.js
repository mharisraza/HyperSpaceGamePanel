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

    if(status == "logout-success") {
        toastr["success"]("Logout Successfully!", "Success");
    }

    if(status == "bad-credentials") {
        toastr["error"]("Wrong Email address or password.", "Error");
    }

    if(status == "user-disabled") {
        toastr["error"]("Your Account is Suspended, please contact us to be un-block.", "Account Suspended!")
    }

    if(status == "USER_DOESNT_EXIST_WITH_PROVIDED_EMAIL") {
        toastr["error"]("User doesn't exist with provided email address, please try correct email address.", "Wrong Email Address!")
    }

    if(status == "TOKEN_SENT_SUCCESSFULLY") {
        toastr["success"]("We've sent your password reset link on your email address.", "Reset Link Sent!");
    }

    if(status == "INVALID_TOKEN") {
        toastr["error"]("The request you made is expired or invalid.", "Invalid Request!");
    }

    if(status == "UPDATE_NEW_PASSWORD") {
        toastr["info"]("Please update your new password here.", "Info!");
    }

    if(status == "PASSWORD_UPDATED") {
        toastr["success"]("Password updated, you can login now!", "Success");
    }

    if(status == "PASSWORD_CONFPW_NOT_MATCH") {
        toastr["error"]("Password or confirm password do not matches.", "Error!");
    }

    if(status == "SOMETHING_WENT_WRONG") {
        toastr["error"]("Sorry, something went wrong!", "Error!");
    }
    
    if(status == "LOGIN_SUCCESS") {
        toastr["success"]("Login successfully!", "Success!");
    }

    if(status == "NOT_LOGIN") {
        toastr["error"]("Please login to continue!", "Error");
    }

    if(status == "CANT_FIND_USER_WITH_PROVIDED_ID") {
        toastr["error"]("Cannot find the user.", "Error");
    }
    
    if(status == "CANT_FIND_ACTIONS") {
        toastr["error"]("There weren't any actions.", "Error");
    }

    if(status == "USER_BANNED_SUCCESSFULLY") {
        toastr["success"]("User banned successfully.", "Success");
    }

    if(status == "USER_UNBANNED_SUCCESSFULLY") {
        toastr["success"]("User unbanned successfully.", "Success");
    }

    if(status == "CANT_FIND_TICKET") {
        toastr["error"]("The ticket is doesn't exist or deleted.", "Error");
    }

    if(status == "REPLY_MESSAGE_ADDED_SUCCESSFULLY") {
        toastr["success"]("Message added successfully.", "Success");
    }

    if(status == "REPLY_MESSAGE_DELETED_SUCCESSFULLY") {
        toastr["success"]("Message deleted successfully.", "Success");
    }

    if(status == "REPLY_MESSAGE_IS_EMPTY") {
        toastr["error"]("Message shouldn't be empty.", "Error");
    }

    if(status == "CANT_ADD_MESSAGE_TICKET_IS_CLOSED") {
        toastr["error"]("Unable to add message to the ticket.", "Error");
    }

    if(status == "TICKET_CLOSED_SUCCESSFULLY") {
        toastr["success"]("Ticket closed successfully.", "Success");
    }

    if(status == "TICKET_UNCLOSED_SUCCESSFULLY") {
        toastr["success"]("Ticket unclosed successfully.", "Success");
    }

    if(status == "TICKET_REPLY_DOES_NOT_EXIST_OR_NOT_ALLOW_TO_DELETE") {
        toastr["error"]("Ticket reply doesn't exist or action weren't allowed.", "Error");
    }

    if(status == "TICKET_REPLY_DOES_NOT_EXIST") {
        toastr["error"]("Ticket reply doesn't exist or deleted.", "Error");
    }

    if(status == "CANT_UNCLOSE_TICKET_CLOSED_BY_ADMIN") {
        toastr["error"]("Cannot unlock the ticket, it was closed by admin", "Error");
    }

    if(status == "WRONG_CREDENTIALS_CONNECTION_FAILED") {
        toastr["error"]("Can't connect to machine, credentials is not correct.", "Error");
    }

    if(status == "MACHINE_ADDED_SUCCESSFULLY") {
        toastr["success"]("Machine added successfully.", "Success");
    }

    if(status == "CANT_FIND_MACHINE") {
        toastr["error"]("The machine is didn't exist or got deleted.", "Error");
    }

    if(status == "MACHINE_DELETED_SUCCESSFULLY") {
        toastr["success"]("Machine and the all associated server are deleted successfully.", "Success");
    }

    if(status == "HOST_NAME_IS_EMPTY") {
        toastr["error"]("Hostname shouldn't be empty", "Error");
    }

    if(status == "HOSTNAME_CONTAINING_SPECIAL_CHAR") {
        toastr["error"]("Hostname shouldn't contain any special characters.", "Error");
    }

    if(status == "VPS_RESTARTED_SUCCESSFULLY") {
        toastr["success"]("Machine restarted successfully, it could take some time to up the associated the game-servers.", "Success!")
    }

    if(status == "HOSTNAME_CHANGED_SUCCESSFULLY") {
        toastr["success"]("Hostname updated successfully.", "Success");
    }
});
