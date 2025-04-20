const apiUrl = 'https://libraryappswc4253.amirulasri.com';
const bearerToken = localStorage.getItem("usertoken");

async function sendEmailToLateUsers() {
    const link = document.getElementById("emailLink");

    link.style.pointerEvents = "none";
    link.style.opacity = 0.5;
    link.textContent = "Sending...";

    try {
        const latereturnurl = apiUrl + "/api/bookborrows/latereturn";
        const response = await fetch(latereturnurl, {
            method: "POST",
            headers: {
                'Authorization': `Bearer ${bearerToken}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        link.style.pointerEvents = "auto";
        link.style.opacity = 1;
        link.textContent = "Send email to late users";
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

async function logout() {
    localStorage.removeItem("usertoken");
    window.location = "login.html";
}

async function getProfile() {
    try {
        const profileurl = apiUrl + "/api/auth/profile";
        const response = await fetch(profileurl, {
            headers: {
                'Authorization': `Bearer ${bearerToken}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            localStorage.removeItem("usertoken");
            alert("Token timeout, relogin needed to continue.");
            window.location = "login.html";
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        var studentnav = document.getElementById("studentnav");
        var managebooknav = document.getElementById("managebooknav");
        var manageborrowbooknav = document.getElementById("manageborrowbooknav");
        var manageusernav = document.getElementById("manageusernav");
        var managelatereturnnav = document.getElementById("managelatereturnnav");

        if(data.role == "ADMIN"){
            studentnav.style.display = "none";
            managebooknav.style.display = "unset";
            manageborrowbooknav.style.display = "unset";
            manageusernav.style.display = "unset";
            managelatereturnnav.style.display = "unset";
        } else if (data.role == "LIBRARIAN") {
            studentnav.style.display = "none";
            managebooknav.style.display = "unset";
            manageborrowbooknav.style.display = "unset";
            manageusernav.style.display = "none";
            managelatereturnnav.style.display = "unset";
        } else {
            studentnav.style.display = "unset";
            managebooknav.style.display = "none";
            manageborrowbooknav.style.display = "none";
            manageusernav.style.display = "none";
            managelatereturnnav.style.display = "none";
        }

    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

getProfile();