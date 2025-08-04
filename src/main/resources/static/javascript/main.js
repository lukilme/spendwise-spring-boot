function greetWorld() {
    alert("hello world")
    console.log("fadsfadsfadsfcdasfads")
}

function salute(name) {
    alert("hello: " + name)
}

function openModal(button, customerId, name) {
    console.log("ID recebido:", customerId);
    document.getElementById("customerIdInput").value = customerId;
    const modal = document.getElementById("deleteModal");
    document.getElementById("name_customer_to_delete").textContent = "Tem certeza que deseja excluir o usuario " + name + " ?";

    const visiblePassword = document.getElementById('visiblePassword').value;

    // Atribui ao campo hidden que será enviado
    document.getElementById('hiddenPassword').value = visiblePassword;
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    console.log(button);
    // document.getElementById('deleteForm').addEventListener('submit', function (e) {
    //     const passwordInput = document.getElementById('adminPassword');
    //     const modalPassword = document.getElementById('modalPassword');
    //     modalPassword.value = passwordInput.value;
    // });
    document.getElementById('deleteForm').addEventListener('submit', function (e) {
        const visible = document.getElementById('visiblePassword').value;
        document.getElementById('hiddenPassword').value = visible;
    });
}

function closeModal() {
    const modal = document.getElementById('deleteModal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
}

function greetWorld() {
    alert("hello world")
    console.log("fadsfadsfadsfcdasfads")
}

function salute(name) {
    alert("hello: " + name)
}

function teste() {
    console.log("OKasd")
}
document.addEventListener('DOMContentLoaded', function () {
        const accountType = document.getElementById('accountType');
        const expirationField = document.getElementById('expirationDateField');
        const expirationDate = document.getElementById('expirationDate');
        console.log(accountType)
        console.log(expirationDate)
        console.log(expirationField)
        function toggleExpirationDate() {
            const value = accountType.value;
            console.log("call")
            console.log("Tipo de conta selecionado:", value);
            if (value === 'CREDIT') {
                expirationField.style.display = 'block';
            } else {
                expirationField.style.display = 'none';
                if (expirationDate) {
                    expirationDate.value = '';
                }
            }
        }

        accountType.addEventListener('change', toggleExpirationDate);

        toggleExpirationDate();
    });
// document.addEventListener('DOMContentLoaded', function () {
//     let currentCustomerId = null;
//     const modal = document.getElementById('deleteModal');
//     const cancelBtn = document.getElementById('cancelBtn');
//     const deleteForm = document.getElementById('deleteForm');
//     const adminPassword = document.getElementById('adminPassword');
//     const customerIdInput = document.getElementById('customerIdInput');
//     const modalPassword = document.getElementById('modalPassword');

//     document.querySelectorAll('.delete-btn').forEach(button => {
//         button.addEventListener('click', function () {
//             console.log("aokas")
//             currentCustomerId = this.getAttribute('data-customer-id');
//             if (currentCustomerId) {
//                 customerIdInput.value = currentCustomerId;
//                 modal.classList.remove('hidden');
//                 modal.classList.add('flex');
//                 adminPassword.focus();
//             }
//         });
//     });

//     cancelBtn.addEventListener('click', function () {
//         console.log("aokas")
//         modal.classList.add('hidden');
//         modal.classList.remove('flex');
//         adminPassword.value = '';
//     });

//     modal.addEventListener('click', function (e) {
//         console.log("aokas")
//         if (e.target === modal) {
//             modal.classList.add('hidden');
//             modal.classList.remove('flex');
//             adminPassword.value = '';
//         }
//     });

//     deleteForm.addEventListener('submit', function (e) {
//         console.log("aokas")
//         if (adminPassword.value.trim() === '') {
//             e.preventDefault();
//             alert('Por favor, digite sua senha para confirmar a exclusão.');
//             adminPassword.focus();
//         } else {
//             modalPassword.value = adminPassword.value;
//         }
//     });
// });