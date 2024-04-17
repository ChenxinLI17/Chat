# **SR03: Application de Chat**



![img](https://lh6.googleusercontent.com/L-We4bygnym7nF7AtyK2ngaSNLU8yAyw1Cu3YxnAb2Fz-iSvApmrMN9qEF-H2GLdBMKtY8SFNjUVyl5B_uKEajOa41QhNvZkK-Pfp1edjeFZ74eA0r9xe0x0ASRR72LjyNsEqSY2gRbiKLnQlBe8jlU)

Zhenyang XU : zhenyang.xu@etu.utc.fr

Chenxin LI : chenxin.li@etu.utc.fr



## **I. Contexte**

1. Compte administrateur :

Mail: [zhenyang.xu@etu.utc.fr](mailto:Zhenyang.xu@etu.utc.fr)

Mot de passe: Xzy000526

2. Version : java 11

3. Outils de codage：IntelliJ IDEA, WebStorm

4. Gitlab :

Git-backend:[ https://gitlab.com/18124672/sr03-chat](https://gitlab.com/18124672/sr03-chat)

Git-frontend:[ https://gitlab.com/ChenxinLI17/sr03_chat_user](https://gitlab.com/ChenxinLI17/sr03_chat_user)

5. Page d'accueil :

Page d'accueil d'utilisateur:[ http://localhost:3000/login](http://localhost:3000/login)

Page d'accueil d'admin:[ http://localhost:8080/login](http://localhost:8080/login)

###  

## **II. Description de l'architecture**

### **1. Interface d’administration**

### ![img](https://lh5.googleusercontent.com/6NI6qYgx4av6DPfHZ59Yym1NztWo4GAydKY4QGXrOsLqZYrRjWJKWC7S-5EaFWVY_gKsZnFGq26hRgdk17AA9Nk22oUqTRmw1Z1BYoEQ3B9Zb8hFUpBI9cfoJ99ioaJjmXMDkj15dR3hGYNQMfq7KIA)

<center><I>Arcitecture MVC d’interface d’administration</I></center>

L'interface d'administration de notre projet permet aux administrateurs de récupérer les listes d'utilisateurs, de mettre à jour les informations sur les utilisateurs et de récupérer les informations sur les chats. Nous choisissons l’architecture MVC.

La partie Contrôleur est gérée par Spring Boot. Le `DispatcherServlet` de Spring Boot agit comme un contrôleur frontal pour traiter toutes les demandes du client.

Le `DispatcherServlet` trouve le contrôleur correspondant pour traiter la demande sur la base des informations de la demande. Lors du traitement de la demande, le contrôleur utilisera les services fournis par la couche de service pour mettre en œuvre la logique métier. Certains de ces services font appel à l'interface de la couche DAO pour réaliser l'opération de persistance des données, qui fait partie du Modèle.

Une fois que le contrôleur finit l’exécution, il renvoie un objet `ModelAndView` au `DispatcherServlet`, qui contient les données du résultat et les informations de vue nécessaires au rendu du résultat. Dans la partie View, nous choisissons le moteur de template `Thymeleaf` pour rendre les données résultantes dans un fichier HTML et le renvoyer au client en tant que réponse finale.



### **2. Interface d’utilisateur**

### ![img](https://lh3.googleusercontent.com/0onw27HBW460wtydgO35tzSawvy26-sgvTR3rh8uZTWTGUGwS0P9UQIv3kHXCGeLtl7yco_2exC1ArHz_unfFbq154fXCwWW6RnpPbM7X3gJ4q0ew7fMy0hCu9KTkHSVfzEze_FFjmJBQxKmObLH_Sc)

<center><i>Arcitecture MVC d’interface d’administration</i></center>

L'interface utilisateur est une structure front-end/back-end distincte, qui comprend deux fonctions principales : l'interface de fonction utilisateur et le salon de discussion.

L'interface fonction utilisateur comprend principalement des fonctions telles que la création de chats et la mise à jour des informations sur l'utilisateur. Le front-end envoie une requête HTTP au back-end en fonction du geste de l'utilisateur. Le back-end répond avec le contrôleur `RestController` qui analyse la requête, traite la logique métier et renvoie les résultats au front-end, qui à son tour rend les résultats sur la page.

La fonctionnalité du salon de discussion est principalement mise en œuvre par le protocole `WebSocket`. La page frontale crée une connexion `WebSocket` avec le serveur `WebSocket`, qui gère les messages sur les canaux de communication, permettant la communication entre les utilisateurs.

Les interactions spécifiques seront expliquées dans la section IV.

###  

## **III. Conception**

### **1. Introduction aux structures de données**

Base de données relationnelle: Mysql

```java
spring.datasource.url=jdbc:mysql://tuxa.sme.utc:3306/sr03p007

spring.datasource.username=sr03p007

spring.datasource.password=dM1qhWbsq0ow
```



![img](https://lh5.googleusercontent.com/acK4TCuupTgQNem74YH1j9iRIjhrK2VUqKExWFICUWQtRo4WUu4XTYdUERAnDExOQ88keDOKFa4W0byfU1gyRGhsIuxyEQ2EX7NiTGz621X1K7Mz7ALcBHuC9NsjY_7gDTtWWaQwVSuHeBClbp9h0VI)

<center><i>structure de la base de données</i></center>

Il y a un total de quatre tables dans la base de données :

- La table `User`

La table `User` est utilisée pour stocker les informations personnelles des utilisateurs. Les administrateurs du backend peuvent décider si un utilisateur est un `admin` et s'il est `active`.

- La table `ChatGroup`

La table `ChatGroup` est utilisée pour stocker les informations sur les groupes de discussion. Elle est liée à la table `User` via la clé étrangère `owner_id` pour stocker le propriétaire de la salle de discussion.

- La table `GroupMember`

La table `GroupMember` est une table intermédiaire qui relie la table `User` et la table `ChatGroup` via deux clés étrangères, `user_id` et `group_id`. Elle est utilisée pour stocker tous les utilisateurs appartenant à un groupe de discussion.

- La table `GroupMessage`

La table `GroupMessage` est utilisée pour stocker l'historique des messages dans chaque groupe de discussion. Bien que le projet ne demande pas de stocker les messages, vous avez décidé de le faire. Elle est reliée à la table `GroupMember` et à la table `ChatGroup` via deux clés étrangères, `user_id` et `group_id`, pour enregistrer les messages envoyés par différents utilisateurs dans différents groupes de discussion.

Note : Dans le code du projet, pour répondre aux exigences, vous avez commenté le code de stockage des messages. Cependant, vous avez toujours créé la table `GroupMessage`. Si vous souhaitez enregistrer et afficher l'historique des messages, il suffit de décommenter le code correspondant.



### **2. Présentation des pages**

Le frontend est conçu avec REACT pour les pages utilisateurs, servant à la discussion entre utilisateurs.

**2.1 Page d'utilisateur**

- **Page de connexion de l'utilisateur** :[ http://localhost:3000/login](http://localhost:3000/login)

Utilisée pour la vérification de la connexion de l'utilisateur et la génération du token. Les utilisateurs non actifs ne peuvent pas se connecter.

- **Page d'inscription de l'utilisateur** :[ http://localhost:3000/register](http://localhost:3000/register)

L'utilisateur doit créer un nouveau compte par courrier électronique et nom. Si cet email n'est pas utilisé par quelqu'un d'autre, alors SpringBoot enverra un email à cet utilisateur pour lui fournir un mot de passe temporaire.

- **Page de chat de l'utilisateur** :[ http://localhost:3000/chatroom](http://localhost:3000/chatroom)

L'utilisateur peut choisir tous les chats de groupe, ceux dont il est le propriétaire ou ceux dont il est membre, et entrer dans ce chat pour discuter avec les autres utilisateurs. L'utilisateur peut également choisir de créer un chat de groupe ou de quitter un chat de groupe. S'il est le propriétaire, il peut inviter d'autres utilisateurs à rejoindre le chat de groupe ou dissoudre le chat de groupe.

- **Page d'informations de l'utilisateur** :[ http://localhost:3000/user_center](http://localhost:3000/user_center)

Après l'inscription du nouvel utilisateur, qui se fait par le mot de passe temporaire reçu par mail, l'utilisateur peut consulter ses informations personnelles et modifier son mot de passe, ainsi que son nom, sur la page des informations de l'utilisateur.

- **Page de création de de la groupe de chat** :[ http://localhost:3000/create_chat_group](http://localhost:3000/create_chat_group)
L'utilisatuer peut créer de nouveaux groupes de chat en fournissant un nom de groupe de chat, une description et un délai d'expiration.

**2.2 Page d'admin**
Le backend est conçu avec SpringBoot pour les pages d'administration, servant à gérer toutes les informations des utilisateurs et des salles de chat.

- **Page de connexion de l'administrateur** :[ http://localhost:8080/login](http://localhost:8080/login)

Utilisée pour la vérification de la connexion de l'administrateur et la génération du token. Les utilisateurs non actifs et non administrateurs ne peuvent pas se connecter.

- **Page d'inscription de l'administrateur** :[ http://localhost:8080/create_user](http://localhost:8080/create_user)

Utilisée pour l'inscription de nouveaux administrateurs.

- **Page de la liste des utilisateurs** :[ http://localhost:8080/admin/user_list](http://localhost:8080/admin/user_list)

Les administrateurs connectés peuvent consulter la liste des utilisateurs et modifier toutes les informations des utilisateurs.

- **Page de modification des informations de l'utilisateur** :[ ](http://localhost:8080/admin/chatgroups)[http://localhost:8080/admin/update_user/id?](http://localhost:8080/admin/update_user/id)

Les administrateurs connectés peuvent consulter la liste des salles de chat.

- **Page de la liste des salles de chat** :[ http://localhost:8080/admin/chatgroups](http://localhost:8080/admin/chatgroups)

Les administrateurs connectés peuvent consulter la liste des salles de chat.

- **Page de modification des informations de la groupe de chat** :[ http://localhost:8080/admin/update_chatgroup/id](http://localhost:8080/admin/update_chatgroup/id)

Les administrateurs connectés peuvent modifier les informations de chaque salle de chat.

- **Page de consultation des messages du groupe de discussion** :[ http://localhost:8080/admin/chatgroup/id/messages](http://localhost:8080/admin/chatgroup/id/messages)

Les administrateurs connectés peuvent consulter les messages historiques de chaque salle de chat. Note : Selon les exigences du projet, nous avons temporairement commenté le code relatif au stockage des messages historiques, de sorte que cette page est actuellement vide. Si vous voulez consulter les messages historiques, vous devez décommenter le code correspondant.

- **Page de consultation des membres du groupe de discussion** :[ http://localhost:8080/admin/chatgroup/id/members](http://localhost:8080/admin/chatgroup/id/members)

Les administrateurs connectés peuvent consulter les membres de chaque groupe de discussion.

###  

## **IV. Explications sur les interaction entre les différente technologies**

### 1. WebSocket

WebSocketServer dispose d'une sessionMap pour gérer les informations relatives à l'utilisateur.

**Structure de la classe userChats :**

```java
public class UserChats {
   private User user;
   private List<ChatGroup> chats;
```

La variable user stocke l' objet utilisateur connecté au salon de discussion, et le variable chats stocke les chats auxquels l‘utilisateur participe.

**Structure du sessionMap :**

```java
public static final Map<Session, UserChats> sessionMap = new ConcurrentHashMap<>();
```

La clé est la session de l'utilisateur actuellement connecté au salon de discussion, qui est utilisée pour gérer les messages du salon de discussion, et la valeur est le userChats de l'objet utilisateur.



**Interaction de la mise en œuvre de WebSocket**

1. **Envoyer une demande au serveur** : Le front-end utilise différentes fonctions `handle` pour gérer les différentes actions des utilisateurs (par exemple, créer un groupe de chat, inviter des utilisateurs, envoyer des messages, etc.). Ces fonctions envoient un objet JSON contenant un certain type de `message` au serveur via `WebSocket`.
2. **Traitement de la demande par le serveur** : Le serveur reçoit ces objets JSON via `jsonObject`, les analyse et exécute les actions correspondantes en fonction des informations et des commandes qu'ils contiennent. Par exemple, si la commande est `getUsers`, le serveur récupérera la liste de tous les utilisateurs. Si la commande est `inviteUsers`, il invitera certains utilisateurs à rejoindre le groupe de chat.
3. **Le serveur renvoie le résultat** : Après avoir exécuté l'action demandée par l'utilisateur, le serveur emballe le résultat dans un nouvel objet JSON, puis l'envoie au client via `WebSocket` avec `session.getBasicRemote().sendText(result.toString());`. Ce résultat peut être une confirmation de réussite, un message d'erreur, ou les données demandées, comme une liste d'utilisateurs, etc.
4. **Le front-end reçoit et traite le résultat** : Le client `WebSocket` du front-end écoute les messages envoyés par le serveur. Lorsqu'il reçoit un nouveau message, il utilise la fonction `useEffect` pour capturer cet événement, puis analyse l'objet JSON pour obtenir le résultat. Ensuite, le front-end peut mettre à jour l'interface utilisateur en fonction de ces résultats. Par exemple, si le résultat est une liste d'utilisateurs, le front-end l'affichera. Si le résultat est un message d'erreur, le front-end l'affichera à l'utilisateur.

Donc, tout le processus est une interaction, le front-end envoie une demande au serveur, le serveur répond et traite la demande, renvoie le résultat, puis le front-end traite ces résultats et met à jour l'interface.



**WebSocketServer reçoit et répond à six types de demandes :**

**@OnOpen**

Cette méthode est exécutée lorsque l'utilisateur se connecte au serveur de chat. Elle prend en entrée une session et une adresse e-mail d'utilisateur. Il cherche l'utilisateur dans la base de données et récupère tous les groupes de chat auxquels l'utilisateur appartient. Ces informations sont ensuite stockées dans une map (sessionMap), et une liste de tous les groupes de chat est renvoyée à l'utilisateur.

**@OnClose**

Cette méthode est exécutée lorsque l'utilisateur se déconnecte du serveur de chat. Elle prend la session et l'adresse e-mail de l'utilisateur et supprime la session de la sessionMap.

**@OnMessage**

Cette méthode est exécutée lorsque le serveur reçoit un message d'un utilisateur. En fonction du contenu du message, différentes actions sont effectuées :

- Si le message reçu de l'avant contient `getUsers` et sa valeur est true, le serveur va chercher tous les utilisateurs de la base de données et les renvoyer à l'avant. Le code correspondant à cette opération est le suivant :

  ```java
  if (jsonObject.has("getUsers") && jsonObject.getBoolean("getUsers")){
      List<User> users = userRepository.findAll();
      List<String> emails = users.stream().map(User::getMail).collect(Collectors.toList());
      result.put("emails", emails);
      session.getBasicRemote().sendText(result.toString());
  }
  ```

- Si le message reçu contient `inviteUsers` et sa valeur est true, le serveur extrait les emails des utilisateurs à inviter du message, vérifie si ces utilisateurs sont déjà membres du groupe de chat, et s'ils ne le sont pas, les ajoute au groupe de chat et renvoie un message de confirmation à l'avant.

  ```java
  else if (jsonObject.has("inviteUsers") && jsonObject.getBoolean("inviteUsers")) {
      // Implementation...
      session.getBasicRemote().sendText(result.toString());
  }
  ```

- Si le message reçu contient `leaveChat` et sa valeur est true, le serveur va vérifier si l'utilisateur qui souhaite quitter le chat est le propriétaire du groupe. Si c'est le cas, le groupe est dissous. Si ce n'est pas le cas, l'utilisateur est simplement retiré du groupe de chat.

  ```java
  else if (jsonObject.has("leaveChat") && jsonObject.getBoolean("leaveChat")) {
      // Implementation...
      session.getBasicRemote().sendText(result.toString());
  }
  ```

- Si le message reçu contient un message de chat normal, ce message est transmis à tous les autres utilisateurs du groupe de chat. Les informations sur le message de chat sont extraites du message reçu.

  ```java
  else if (!msg.isEmpty()) {
      // Implementation...
      sendOthersMessage(result.toString(), session, targetChatGroup.getId());
  }
  ```

Chaque fonction est mise en œuvre en extrayant les informations pertinentes du message reçu à l'aide de `jsonObject`, en effectuant les opérations nécessaires, en créant une réponse à l'aide de `result`, et en envoyant cette réponse à l'avant à l'aide de `session.getBasicRemote().sendText(result.toString());`.

**@OnError**

Cette méthode est exécutée en cas d'erreur dans la communication WebSocket. Elle imprime la pile d'erreur.

**sendOthersMessage**

La méthode `sendOthersMessage` parcourt toutes les sessions utilisateur actives et leurs groupes de chat. Si une session appartient au même groupe de chat que celui du message et n'est pas la session de l'utilisateur qui a envoyé le message, alors le message est envoyé à cette session. Cela permet de diffuser le message à tous les autres membres du groupe de chat.



### 2. Rest API (React + Spring)

Requête : Chaque fois qu'un utilisateur accède à une URL, une requête de “get” est envoyée au serveur Sprig par le biais de fetch API lorsque le composant est monté pour la première fois. Pour les fonctions qui nécessitent la soumission de formulaires, comme la création d'un chat de groupe, la modification des informations sur l'utilisateur, etc., nous choisissons d'envoyer une requête “post” en utilisant l'API fetch, en plaçant les informations dans le corps de la requête de sorte que le contrôleur puisse facilement accéder à ces paramètres. Pour les requêtes get, le jeton est placé dans l'attribut Authorization de l'en-tête HTTP, pour les requêtes post, le jeton est placé dans le corps de la requête HTTP.

**Repsonse :**

```java
public class Response {
    private String code;
    private String msg;

    @JsonRawValue
    private Object data;
```

Nous spécifions un format uniforme pour la réponse que le contrôleur renvoie au front-end. La variable code est le code de la réponse, la variable msg est une description du résultat du traitement spécifique du contrôleur, tel que "update successful, invalid password or username", et la variable data contient les données  de résultat qui doivent être rendues sur la page après la requête du front-end.

Lorsque le front-end reçoit la réponse, il détermine d'abord si le code de réponse est 200. Si ce n'est pas le cas, il imprime le message d'erreur sur la page. Si le code de réponse est 200, il traite la réponse, par exemple en imprimant le message "create success", en affichant les informations de l'utilisateur actuel, y compris son nom et son mail, etc.

Cette approche nous permet de mieux gérer et traiter les requêtes et les réponses.



## **V. Sécurité**

### **1. Jeton d’authentification**

```java
// Jetons de session cryptés
String sessionToken = session.getId();

String encryptedSessionToken = AesUtil.*encrypt*(u.getId() + ":" + sessionToken);

// Stockage des jetons de session dans les cookies
Cookie cookie = new Cookie("X-Session-Token", encryptedSessionToken);

cookie.setHttpOnly(true);

cookie.setMaxAge(60 * 60); // Fixe la durée de validité du cookie, 1 heure

response.addCookie(cookie);
```

Nous utilisons l'algorithme de chiffrement `AES` pour générer un jeton pour chaque utilisateur connecté et le stockons dans un cookie. Le jeton est composé de l'ID de l'utilisateur et de l'ID de session, et nous le nommons `X-Session-Token` et le stockons dans le cookie.

Ensuite, nous utilisons un intercepteur appelé `SessionTokenInterceptor` pour intercepter les requêtes.

- Récupérer tous les cookies de la requête et trouver le cookie nommé `X-Session-Token`.

- Si nous trouvons le cookie, nous récupérons sa valeur (c'est-à-dire le jeton de session chiffré).

- Si nous ne trouvons pas le cookie ou si le jeton est vide, nous redirigeons l'utilisateur vers la page de connexion et renvoyons false pour indiquer que la requête n'a pas été validée.

- Si nous trouvons le jeton de session, nous utilisons la méthode de déchiffrement decrypt de la classe `AESUtil` pour déchiffrer le jeton.
- Le jeton déchiffré est composé de deux parties séparées par `:` : `UserID` et `SessionID`.
- Nous appelons la méthode `getOptionalUserById` de `UserService` pour obtenir un objet `Optional<User>` en utilisant l'ID de l'utilisateur déchiffré.

- Nous utilisons la méthode `getSession` de `HttpServletRequest` pour obtenir l'objet de session actuel (s'il n'existe pas, null est renvoyé).
- Nous comparons  `SessionID` déchiffré avec  `SessionID` de l'objet de session actuel.
- Si  `SessionID` correspondent et que l'objet de session existe, nous définissons l'utilisateur comme attribut `loggedInUser` de la session et renvoyons true pour indiquer que la requête a été validée.

- Si  `SessionID` ne correspondent pas ou si l'objet de session n'existe pas, nous redirigeons l'utilisateur vers la page de connexion et renvoyons false pour indiquer que la requête n'a pas été validée.

Cela nous permet de prévenir l'accès à des pages nécessitant une connexion en évitant la copie de session ou de jeton par d'autres personnes. Même si le même compte se connecte à partir de différents navigateurs sur le même ordinateur, des jetons différents seront générés.

### **2. Stockage crypté par mot de passe**

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
```

Lorsque nous stockons les mots de passe dans la base de données, nous utilisons l'algorithme `BCrypt` de la bibliothèque `Spring Security`. Nous utilisons le hachage avec salage pour crypter les mots de passe et les stocker dans la base de données, afin de prévenir les intrusions dans la base de données et le vol de mots de passe.

Lorsque nous utilisons l'algorithme `BCrypt`, le mot de passe est transformé en une valeur de hachage irréversible à l'aide d'une fonction de hachage cryptographique puissante. De plus, un sel (ou une valeur aléatoire) est généré pour chaque mot de passe, puis il est combiné avec le mot de passe avant d'être haché. Cela ajoute une couche de sécurité supplémentaire en rendant les attaques de hachage par force brute plus difficiles, car chaque mot de passe a son propre sel.

En utilisant `BCrypt`, les mots de passe stockés dans la base de données sont protégés même en cas d'intrusion dans la base de données, car il est extrêmement difficile de retrouver le mot de passe d'origine à partir de la valeur de hachage et du sel. Cela garantit que les mots de passe des utilisateurs restent confidentiels même en cas de compromission de la base de données.

### **3. Validation du format de mot de passe**

```java
const passwordCheck = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!_@#$%^&*])[a-zA-Z\d!_@#$%^&*]{8,}$/;
```

En plus du mot de passe temporaire que les utilisateurs reçoivent lors de leur inscription, nous avons défini des exigences pour la modification du mot de passe afin d'améliorer la sécurité du compte.

Cette expression régulière exige que le mot de passe satisfasse aux conditions suivantes :

- Il doit contenir au moins une lettre minuscule.
- Il doit contenir au moins une lettre majuscule.
- Il doit contenir au moins un chiffre.
- Il doit contenir au moins un caractère spécial (point d'exclamation, tiret bas, arobase, hashtag, dollar, pourcentage, esperluette ou astérisque).
- Il doit être composé d'au moins 8 caractères.



### **4. JWT**

![img](https://lh5.googleusercontent.com/awxM0w16fbpfifsu6kDyIucZyf7V8Ndp9UguoyHI3WssffEmHDzh7NHYBvr8Pp-b0BmJTi704xqFMNR2WiIDO8lom9ywqvRYW9Pm5edDSQ3lEr5Cby-UwG_gB5kza1SJ-yKO2EaSGfDr4wSQl4sOLEw)

<center><i>Fonctionnement de JWT token</i></center>

Tout d'abord, le front-end envoie son mail d'utilisateur et son mot de passe à l'interface du back-end via un formulaire web.

Le back-end vérifie le nom d'utilisateur et le mot de passe et utilise les données contenant l'identifiant de l'utilisateur et l'identifiant de la session courante comme JWT Payload, formant ainsi un jeton JWT.

Le back-end renvoie la jeton JWT au front-end en tant que résultat d'une connexion réussie. Le front-end enregistre le résultat renvoyé dans le navigateur et supprime le jeton JWT enregistré lors de la déconnexion.

Le front-end place le jeton JWT dans l'attribut Authorization de l'en-tête HTTP de chaque requête lors du demande.

Le back-end vérifie la validité du jeton JWT transmis par le front-end, par exemple si la signature est correcte, si elle a expiré, etc.

Après vérification, le back-end analyse les informations sur l'utilisateur contenues dans le jeton JWT, effectue des opérations logiques et renvoie le résultat.



## **VI. Résultat eco-index (plugin greenit)**

![img](https://lh4.googleusercontent.com/Tju_26a3WQAzx-GUgGtb2MEAGMgFgMEXspuL_QJHr8j5IwXDHpyCAtruyzHj_bRYMOFCjZxnoIoV8zV3jVHjMydJ7CV3Gho30ZAKVy499_-w94BPsnM_9ubNcZavwyI7zNx6ay3ArMz8chmWJbBEoCQ)

<center><i>Résultats des tests GreenIT de la page utilisateur</i></center>

### **![img](https://lh4.googleusercontent.com/5H6ueUz0Owu-E1BOYFa6Wu_uj51iKh-aVrR0jVHmfVwodhmnVnJFQ48in_8A5s_Xrf_toEmLBZxClKT3SLVKFFsKupa2z7WWQd-sPBX9Qhyo9g2LckmcsVtL-VgkkPUK_BTxe8nJTldqohnj_S_-NYo)**

<center><i>Résultats des tests GreenIT de la page d'administration</i></center>



## **VII. Pistes d'amélioration**

1. Mise en page : adaptation de la fenêtre du navigateur, affichage de la page en fonction de la taille de la fenêtre
2. Problème de sécurité mineur : limitation du nombre de fois qu'un même utilisateur peut se connecter au cours d'une période donnée ; après un certain nombre de fois, l'utilisateur ne pourra plus se connecter.
3. Invitation des membres : Lorsqu'un propriétaire de groupe invite un nouvel utilisateur à rejoindre le groupe de discussion, les autres utilisateurs de la même salle de discussion doivent rafraîchir la page pour voir le nouvel utilisateur.Nous espérons améliorer ce problème.
4. Nous n'avons pas trouvé une opportunité adéquate pour supprimer les discussions de groupe expirées. Vérifier si un groupe de discussion a expiré lors du démarrage de Spring Boot est trop tard, car il peut y avoir des discussions expirées depuis longtemps. D'autre part, vérifier la validité des groupes de discussion à chaque fois qu'un utilisateur envoie un message est une utilisation excessive des ressources.