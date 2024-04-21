/*//scripte de spinner-wrapper de l actualisation
const spinnerWrapperEl=document.querySelector(".spinner-wrapper");
window.addEventListener('load',()=>{
    spinnerWrapperEl.style.opacity='0';

    setTimeout(()=>{
        spinnerWrapperEl.style.display='none';
    },10)
})*/

const spinnerWrapperEl = document.querySelector(".spinner-wrapper");

document.addEventListener('DOMContentLoaded', () => {
    // Mettez à jour l'opacité après le chargement de la page
    spinnerWrapperEl.style.opacity = '0';

    // Attendez la fin de l'animation CSS (vous pouvez ajuster le temps si nécessaire)
    const animationTime = 100; // 0.5 secondes (500 millisecondes) comme exemple

    setTimeout(() => {
        // Cachez le spinner après l'animation
        spinnerWrapperEl.style.display = 'none';
    }, animationTime);
});


//pour la confirmation de la suppresion
function confirmDelete(event) {
    // Affichez la boîte de dialogue de confirmation
    var confirmDelete = confirm("Voulez-vous vraiment supprimer cet élément ?");

    // Si l'utilisateur clique sur "Annuler", annulez l'événement de suppression
    if (!confirmDelete) {event.preventDefault();}
}

