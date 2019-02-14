# Talk IRC "WHATSUP lite" (1698T_NFP103_2019)

##Spécification du besoin
On souhaite réaliser un système client/serveur permettant de communiquer à plusieurs simultanément sur le réseau. 

###Coté client :

le client envoie des messages (texte) au serveur.
le client accepte les commandes suivantes saisies au clavier :
1. _connect <surnom> <machine> <port> : se connecte au serveur et se déclare avec le surnom fourni en paramètre.
2. _quit : quitte. Le client quitte et le signale au serveur.
3. _who : demande au serveur la liste des utilisateurs connectés.

###Coté serveur :
le serveur diffuse tous les messages (texte) qu'il reçoit d'un de ces clients vers tous les autres clients connectés et connus.<br/>le serveur accepte les commandes suivantes :
provenant d'un client ou d'une saisie au clavier :<br/>
- _who : idem que _who coté client.
######provenant du clavier :
- _kill <surnom> : coupe la connexion du client correspondant au <surnom> et en informe les clients restants.
- _shutdown : pour arrêter le serveur.
######provenant d'un client :
- _connect : le serveur informe les autres clients de l'arrivée du client émetteurde cette commande.
- _quit : le serveur informe les autres clients du départ du client émetteur de cette commande.

######Quand ceci est réalisé vous pouvez aussi ajouter les fonctionalité suivantes
1. Chat room. ajouter les commandes _create <room> et _join <room>
2. Communication de groupe asynchrone _create <groupe> _add <surnom> <groupe>
3. Chat peer-to-peer (entre 2 utilisateurs)
4. Partage de documents _share <file>


