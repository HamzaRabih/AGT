//---------------------------------------------
// Initialisation des variables
//-------------------------------------------

var i = 0;
var tbody = document.getElementById('tbody1');

//Un élément span avec l'id 'notification' a été utilisé pour appliquer la fonction playNotificationSound(), permettant ainsi que le son de la notification soit entendu sur toutes les pages HTML.' +
var notificationNumber = document.getElementById('notification');
if (notificationNumber!=null) {
    notificationNumber.style.display = 'none';
}

//le compteur de notification
var notifiClassList = document.getElementsByClassName('notif2');

var spanNotifNum=document.getElementById('spanNotifNum');

if (spanNotifNum.textContent==0) {
    spanNotifNum.style.display='none';
};


//-------------------------------------------
// Websocket
//-------------------------------------------

var socket = new SockJS('ws');// Cela signifie que les clients peuvent se connecter à votre application
                              // via WebSocket en utilisant l'URL /ws.

var stompClient = Stomp.over(socket);//crée un client STOMP sur le dessus du socket WebSocket créé précédemment.
                                      // Le client STOMP est utilisé pour envoyer et recevoir des messages STOMP
                                       // sur la connexion WebSocket établie.

//STOMP (Simple Text Oriented Messaging Protocol). STOMP est un protocole de communication basé sur des messages,
// souvent utilisé avec WebSocket pour permettre la communication bidirectionnelle entre un client et un serveur.

// Connexion au serveur WebSocket
stompClient.connect({}, function (frame) {
    //console.log('WebSocket connection opened.');

    i = 0;
    // Abonnement à la destination spécifique de l'utilisateur
    stompClient.subscribe('/user/topic/private', function (message) {


        // Traitement de la tâche reçue via WebSocket
        // La variable 'payload' peut contenir vos objets (tâche et notification)
        var payload = JSON.parse(message.body);
        var tache = payload[0];
        var notification = payload[1];

       //pour le sonnete de notif
        //notificationNumber.click();
        // Ajout d'un compteur des notifications
        //i++;
        //showNotification(i);

        // Mise à jour du tableau avec la nouvelle tâche dans MesTaches.Html
        if (tbody!=null) {updateTable(tache);}

        // Mise à jour de la liste des notifs dans notifications.Html
        updateTableNotif(notification);

        //Cette fonction permet d'afficher le nombre des notifications non lues.
        afficherNotif();


    });
});

// Gestion de la fermeture de la connexion WebSocket
socket.onclose = function (event) {
  //  console.log('WebSocket connection closed: ', event);
};



//---------------------------------------------------
//les fonctions
//---------------------------------------------------
// Fonction pour afficher le nombre de notifications
function showNotification(notif) {
    notificationNumber.textContent = notif;
    // Mettre à jour le nombre de notifications dans les éléments ayant la classe 'notif2'
    for (var j = 0; j < notifiClassList.length; j++)
    {notifiClassList[j].textContent = notif;}
}




// Fonction pour mettre à jour le tableau avec une nouvelle tâche
function updateTable(tache) {
    var tbody = document.getElementById('tbody1');
    // Vérifier si le tbody existe dans la page html
    if (tbody != null) {
        var tr = document.createElement('tr');
        //tr.classList.add('table-border');


        var td1 = document.createElement('td');
        var td2 = document.createElement('td');
        var td3 = document.createElement('td');
        var td4 = document.createElement('td');
        var td5 = document.createElement('td');
        var tdT = document.createElement('td');
        var td6 = document.createElement('td');
        var td7 = document.createElement('td');
        var td9 = document.createElement('td');
        var td8 = document.createElement('td');
        var td10 = document.createElement('td');
        var i = document.createElement('i');
        var StrongforTd1 = document.createElement('strong');
        StrongforTd1.textContent = tache.nomtache;
        i.appendChild(StrongforTd1);
        td1.appendChild(i);
        td7.textContent = tache.utilisateur.prenom+" "+tache.utilisateur.nom;
        td4.textContent = tache.dureestime;
        td9.textContent=tache.priorite.nompriorite;
        td10.textContent = tache.idtache;
        if (tache.dateouverture == null) {td2.textContent = '';}
        else {
            var dateOuverture = new Date(tache.dateouverture);
            var formattedDate =  ('0' + dateOuverture.getDate()).slice(-2) + '/' +('0' + (dateOuverture.getMonth() + 1)).slice(-2) + '/' + dateOuverture.getFullYear().toString().slice(-2);
            td2.textContent = formattedDate;
        }
        if (tache.dateobjectif == null) {td3.textContent = '';}
        else {
            var dateObjectif = new Date(tache.dateobjectif);
            var formattedDate =  ('0' + dateObjectif.getDate()).slice(-2) + '/' +('0' + (dateObjectif.getMonth() + 1)).slice(-2) + '/' + dateObjectif.getFullYear().toString().slice(-2);
            td3.textContent = formattedDate;
        }
        if (tache.dateTermineTache == null) {tdT.textContent = '';}
        else {
            var dateCloture = new Date(tache.dateTermineTache);
            var formattedDate =  ('0' + dateCloture.getDate()).slice(-2) + '/' +('0' + (dateCloture.getMonth() + 1)).slice(-2) + '/' + dateCloture.getFullYear().toString().slice(-2);
            tdT.textContent = formattedDate;
        }





        /*
        // Définir le contenu et les attributs de la colonne de statut
       if (tache.statut === "En attente") {
           td5.textContent = "En attente";
           td5.setAttribute("class", "btn badge bg-label-warning me-1");
           td5.setAttribute("data-id", tache.idtache);
           td5.addEventListener('click', () => modifyStatus(td5));
       }*/

        // Définir le contenu et les attributs de la colonne de statut
        if (tache.statut === "En attente") {
            var btn = document.createElement('button');
            btn.textContent="En attente";
            btn.setAttribute("class", "btn rounded-pill badge bg-warning capitalize-first");
            btn.setAttribute("style","width: 100px !important");
            btn.setAttribute("data-id", tache.idtache);
            btn.addEventListener('click', () => modifyStatus(btn));
            td5.classList.add("text-sm-center")
            td5.appendChild(btn);
        }
        if (tache.statut === "Programmée") {
            var btn = document.createElement('button');
            btn.textContent="Programmé";
            btn.setAttribute("class", "btn rounded-pill bg-primary badge capitalize-first");
            btn.setAttribute("data-id", tache.idtache);
            btn.addEventListener('click', () => modifyStatus(btn));
            td5.classList.add("text-sm-center")
            td5.appendChild(btn);
        }

       //Priorité
        if (tache.priorite.nompriorite=== "Urgent") {
            td9.setAttribute("class", "");
        }

        if (tache.priorite.nompriorite=== "Important") {
            td9.setAttribute("class", "");
        }

        if (tache.priorite.nompriorite=== "Normal") {
            td9.setAttribute("class", "");
        }
        if (tache.priorite.nompriorite=== "En attente") {
            td9.setAttribute("class", "");
        }

        td6.textContent = tache.dureretarde;
        td8.textContent = tache.performance+"%";


        // Ajouter les éléments à la ligne et la ligne au tbody
        tr.appendChild(td1);
        tr.appendChild(td10);
        tr.appendChild(td7);
        tr.appendChild(td9);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        tr.appendChild(tdT);
        tr.appendChild(td5);
        tr.appendChild(td6);
        tr.appendChild(td8);
       // tbody.appendChild(tr);

        tbody.insertBefore(tr, tbody.firstChild);

    }
}

// Mettre à jour la liste des notifications dans le fichier notifications.html
function updateTableNotif(notification) {
    var cardNotif = document.getElementById('cardNotif');

    // Vérifier si cardNotif existe dans la page html
    if (cardNotif != null) {

        // Créer un élément div avec les classes et le style nécessaires
        var toastDiv = document.createElement('div');
        toastDiv.className = 'bs-toast toast  bg-success show';
        toastDiv.style.width = '100%';

        // Créer la partie du header
        var toastHeader = document.createElement('div');
        toastHeader.className = 'toast-header';

        // Ajouter l'icône au header
        var bellIcon = document.createElement('i');
        bellIcon.className = 'bx bx-bell me-2';
        toastHeader.appendChild(bellIcon);

        // Ajouter la partie "Bootstrap" à droite du header
        var titleDiv = document.createElement('div');
        titleDiv.className = 'me-auto fw-semibold';
        titleDiv.textContent = 'Bootstrap';
        toastHeader.appendChild(titleDiv);

        // Ajouter la partie date au header
        var dateSmall = document.createElement('small');
        dateSmall.textContent = notification.datenotif; // Assurez-vous que p.datenotif est défini
        toastHeader.appendChild(dateSmall);

        // Ajouter le header au div principal
        toastDiv.appendChild(toastHeader);

        // Créer la partie du corps du toast
        var toastBody = document.createElement('div');
        toastBody.className = 'toast-body';
        toastBody.textContent = notification.detail; // Assurez-vous que p.detail est défini
        toastDiv.appendChild(toastBody);

        // Récupérer l'élément parent de cardNotif
        var parentElement = cardNotif.parentNode;

        // Insérer toastDiv avant cardNotif dans le parent
        parentElement.insertBefore(toastDiv, cardNotif);
    }

}


// Fonction pour modifier le statut d'une tâche avec un clic
function modifyStatus(button) {
    // Obtenir l'ID de la tâche et le statut actuel à partir des attributs data
    const tacheId = button.getAttribute("data-id");

    // Envoyer une requête AJAX pour mettre à jour le statut de la tâche
    $.ajax({
        url: `tasks/${tacheId}`,
        method: 'GET',
        success: function (data) {
            console.log(data);

            // Mettre à jour l'attribut data-statut du bouton cliqué
            var Varclasss;
            if (data.statut === "En cours") {
                Varclasss = "btn rounded-pill badge bg-info capitalize-first";
                button.textContent = data.statut;
                button.setAttribute("class", Varclasss);
                button.setAttribute("style","width: 100px !important");
                messageNotif();

            }
            if (data.statut === "Terminée") {
                Varclasss = "btn rounded-pill bg-success badge capitalize-first ";
                button.textContent = data.statut;
                button.setAttribute("class", Varclasss);
                button.setAttribute("style","width: 100px !important");
                messageNotif();

            }
            if (data.statut === "À refaire") {
                Varclasss = "btn rounded-pill bg-danger badge capitalize-first";
                button.textContent = data.statut;
                button.setAttribute("class", Varclasss);
                button.setAttribute("style","width: 100px !important");
                messageNotif();

            }

            <!--th:id="'dureretardeCol_' + ${p.idtache}" pour recuperer la valeur par l attribut id avec l'idtache dans javacript-->
           /* // Mettre à jour la colonne dureretarde avec la nouvelle valeur
            var dureretardeCol = document.getElementById('dureretardeCol_'+ tacheId); // Remplacez par l'ID correct
                dureretardeCol.textContent = data.dureretarde;

                // Mettre à jour la colonne performance avec la nouvelle valeur
            var performanceCol = document.getElementById('performanceCol_'+ tacheId); // Remplacez par l'ID correct
                performanceCol.textContent = data.performance + '%';
            */



        }


    });
}


//fonction pour afficher les msgs
function messageNotif(){
    // Create an instance of Notyf
    var notyf = new Notyf();
    // Display a success notification
    notyf.success('Le statut a été modifiée avec succès.');
}

//pour lire les notifs non lues
function readNotif() {
    // Appeler le backend pour marquer toutes les notifications non lues comme lues
    $.ajax({
        url: 'lireLesNotif',
        type: 'GET',
        contentType: 'application/json',
        success: function () {
           // console.log('Toutes les notifications non lues ont été marquées comme lues.');
            // Mettez à jour la page ou effectuez d'autres actions si nécessaire
        },
        error: function (error) {
            console.error('Erreur :', error);
        }
    });
}

//Cette fonction permet d'afficher le nombre des notifications non lues.
function afficherNotif() {
    // Appeler le backend pour marquer toutes les notifications non lues comme lues
    $.ajax({
        url: '/afficherNotifNonLu',
        type: 'GET',
        contentType: 'application/json',
        success: function (numNotifNonLu) {

            //traitement
            console.log(numNotifNonLu)
           var spanNotifNum=document.getElementById('spanNotifNum');
            if (spanNotifNum!=null) {
                spanNotifNum.style.display = '';
                spanNotifNum.textContent = numNotifNonLu;
            }
        },
        error: function (error) {
            console.error('Erreur :', error);
        }
    });
}