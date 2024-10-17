//----------------------configuration data table
//des touts les interfaces sauf : Gestion Tache Admin ,mesTache , Suivi des taches
/*
$(document).ready(function () {
    $('#example').DataTable({
        info: false,
        "lengthMenu":[10, 25, 50],
        "language": {
            "lengthMenu": "Afficher _MENU_ entrées",
            "info": "Affichage de l'entrée _START_ à _END_ sur un total de _TOTAL_ entrées",
            "infoEmpty": "Affichage de l'entrée 0 à 0 sur un total de 0 entrées",
            "infoFiltered": "(filtré à partir de _MAX_ entrées au total)",
            "search": "Rechercher :",
            "paginate": {
                "first": "Première",
                "last": "Dernière",
                "next": "Suivant",
                "previous": "Précédent"
            }
        },
        "dom": '<"top"f>t<"bottom"lp>'
    });
});
 */

$(document).ready(function () {
    // Récupérer la pagination sauvegardée (ou utiliser 10 par défaut)
    const savedLength = parseInt(localStorage.getItem('datatableLength')) || 10;
    console.log('Pagination sauvegardée :', savedLength); // Vérification de la valeur récupérée

    // Initialiser la DataTable
    var table = $('#example').DataTable({
        info: false,
        lengthMenu: [10, 25, 50],
        pageLength: savedLength, // Appliquer la pagination sauvegardée
        language: {
            "lengthMenu": "Afficher _MENU_ entrées",
            "search": "Rechercher :",
            "paginate": {
                "first": "Première",
                "last": "Dernière",
                "next": "Suivant",
                "previous": "Précédent"
            }
        },
        dom: '<"top">t<"bottom"lp>',
        order: []
    });

    // Événement lorsque l'utilisateur change la pagination
    table.on('length.dt', function (e, settings, len) {
        console.log('Pagination changée :', len); // Log pour vérifier
        localStorage.setItem('datatableLength', len); // Sauvegarder la pagination choisie
    });

    // Recherche en direct
    $('#rechercher').on('keyup', function () {
        table.search(this.value).draw();
    });

});
