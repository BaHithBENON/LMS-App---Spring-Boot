// login.js
document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Empêche la soumission traditionnelle

    const email = document.querySelector('input[name="email"]').value;
    const password = document.querySelector('input[name="password"]').value;

    const data = {
        email: email,
        password: password
    };

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // Rediriger ou gérer la réponse ici
    })
    .catch((error) => {
        console.error('Error:', error);
    });
});
