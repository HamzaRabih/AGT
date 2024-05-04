//----------------------configuration data table
//des touts les interfaces sauf : Gestion Tache Admin ,mesTache , Suivi des taches
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
