/*//scripte de spinner-wrapper de l actualisation
const spinnerWrapperEl=document.querySelector(".spinner-wrapper");
window.addEventListener('load',()=>{
    spinnerWrapperEl.style.opacity='0';

    setTimeout(()=>{
        spinnerWrapperEl.style.display='none';
    },10)
})*/
//----------------------scripte0
//sipnner d actualisation
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
//----------------------/scripte0

//----------------------scripte1
//pour la confirmation de la suppresion
function confirmDelete(event) {
    // Affichez la boîte de dialogue de confirmation
    var confirmDelete = confirm("Voulez-vous vraiment supprimer cet élément ?");
    // Si l'utilisateur clique sur "Annulée", annulez l'événement de suppression
    if (!confirmDelete) {event.preventDefault();}
}
//----------------------/scripte1


//----------------------scripte2
//pour masquer la date d'ouverture dans, cas de tâche indexé
var selecteETacheParente1 = document.getElementById("tacheparente1");
var selecteETacheParente = document.getElementById("tacheparente");
var inputEDateOuverture = document.getElementById("DateOuverture");
var inputEDateOuverture1 = document.getElementById("DateOuverture1");
function validationTacheParente2() {
    if (selecteETacheParente !=null) {
        if (selecteETacheParente.value !=(-1)) {inputEDateOuverture.disabled = true;}
    }
    if (selecteETacheParente1 !=null) {
        if (selecteETacheParente1.value !=(-1)) {inputEDateOuverture1.disabled = true;}
    }
    if (selecteETacheParente !=null) {
        if (selecteETacheParente.value ==(-1)) {inputEDateOuverture.disabled = false;}
    }
    if (selecteETacheParente1 !=null) {
        if (selecteETacheParente1.value ==(-1)) {inputEDateOuverture1.disabled = false;}
    }
}

// Ajouter un écouteur d'événements à l'élément selecteTacheParente
if (selecteETacheParente!=null) {
    selecteETacheParente.addEventListener("change", validationTacheParente2);

}
if (selecteETacheParente1!=null) {
    selecteETacheParente1.addEventListener("change", validationTacheParente2);
}
//----------------------/scripte2


