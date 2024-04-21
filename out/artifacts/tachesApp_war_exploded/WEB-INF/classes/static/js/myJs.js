//scripte de spinner-wrapper de l actualisation
const spinnerWrapperEl=document.querySelector(".spinner-wrapper");
window.addEventListener('load',()=>{
    spinnerWrapperEl.style.opacity='0';

    setTimeout(()=>{
        spinnerWrapperEl.style.display='none';
    },10)
})


//pour la confirmation de la suppresion
function confirmDelete(event) {
    // Affichez la boîte de dialogue de confirmation
    var confirmDelete = confirm("Voulez-vous vraiment supprimer cet élément ?");

    // Si l'utilisateur clique sur "Annuler", annulez l'événement de suppression
    if (!confirmDelete) {event.preventDefault();}
}

