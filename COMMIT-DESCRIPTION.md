# Implemented machine(VPS) module
`desc`: Implemented machine module where admin can add vps to the admin panel so that they can add new game-servers
Machine details are coming dynamically.

# implemented support[ticket] module
`desc`: Implemented ticket module where user can request a query for their problems to the admin.
user can reply to the admin message to the existing ticket (can close or unclose the ticket[not if it closed by admin])
user can know about if admin responded to the ticket via Status[text]
admin reply will show with admin badge and admin name with glowing green badge for better experience.
secured the ticket module, will implement some functionalities to it later (like getting alert if got response)

# implemented ban & unban user functionality.
`desc`: Implemented ban & unban functionality where admin can ban & unban user if they violate the rules.
also added loader (on every submission)

# designed user & admin dashboard:
`desc`: Design user and admin dashboard, added Admin Controller, implemented user list where admin can see all the user and their status
registration date.

# updated UI, fix prev bugs of forgot-pwd module
`desc`: UPDATED UI to cool new user interface, fix some prev bugs, enhanced forgot-passwd module

# implemented forgot-pwd module:
`desc`: implemented forgot password module using MAIL API with gmail service everything based on token
if token expired you'll need to make a new request for new token, after successfully updated new passwd
manually expiring token so user can't use it again.

# implemented login logic:
`desc`: implemented login logic with logout like if you logout you'll get success alert "Logout success"
if your role is: ADMIN, you'll be redirect to ADMIN Dashboard otherwise user dashboard.
also checking bad-credentials or user suspension status

# configure spring security, modified registration module:
`desc`: configured spring security, enhance registration module, fix prev bugs

# update modules status
`desc`: updated module stats

# implemented registration logic:
`desc`: implemented registration logic with errors and other things.

# Merge branch 'master' of github.com:mharirsaza/HyperSpaceGamePanel:
`desc`: merge branch 'master';

# designed register page and navbar:
`desc`: designed register page and navbar and other little things.

# INITIALIAZE PROJECT:
`desc`: initialiaze whole project