# Feuille de Route des Sprints - Framework Passerelle

## Sprint 0 : Interception des requêtes (Front Controller)
* **Principe :** Mettre en place le point d'entrée unique de l'application. On configure un Servlet central (`FrontController`) mappé sur `/` dans le `web.xml`. Son rôle est d'intercepter toutes les requêtes HTTP entrantes et d'extraire l'URL appelée (`getRequestURI()`).

---

## Sprint 1 : Scan et détection des contrôleurs
* **Principe :** Identifier les classes qui jouent le rôle de contrôleur. On crée l'annotation de classe `@Controller`. Au démarrage de l'application, le framework scanne le dossier du package spécifié (`packageToScan`), charge les classes via le `ClassLoader` et filtre celles qui possèdent l'annotation `@Controller`.

---

## Sprint 2 : Cartographie des URL et méthodes (Mapping)
* **Principe :** Associer chaque URL à une méthode précise d'un contrôleur. On crée une annotation de méthode (`@Url`) et une classe de structure `Mapping` (contenant la classe et la méthode). Lors du scan au démarrage, le framework parcourt les méthodes des contrôleurs et remplit un dictionnaire central (`HashMap<String, Mapping>`) où la clé est `"METHOD:URL"` et la valeur est le `Mapping` correspondant.

---

## Sprint 3 : Exécution dynamique de la méthode (Invocation)
* **Principe :** Exécuter la logique métier correspondant à l'URL demandée. Quand une requête arrive, le `FrontController` cherche l'URL dans la `HashMap`. S'il la trouve, il instancie dynamiquement la classe du contrôleur et invoque la méthode associée en utilisant la Réflexion Java (`method.invoke()`).

---

## Sprint 4 : Injection simple des paramètres
* **Principe :** Récupérer les paramètres simples envoyés par le client via la requête (`request.getParameter()`), effectuer le transtypage (conversion de `String` vers `int`, `double`, `Date`, etc.) et les injecter directement dans les arguments de la méthode du contrôleur.

---

## Sprint 5 : Restitution MVC, `ModelView` et Résolution de Vue
* **Principe :** Séparer la logique métier de l'affichage. La méthode retourne un objet `ModelView` contenant le nom logique de la vue et les données à transmettre. Le `FrontController` applique les règles de **préfixe** (ex: `/WEB-INF/views/`) et de **suffixe** (ex: `.jsp`) configurées dans l'application pour reconstituer le chemin d'accès réel, injecte les données dans la requête (`request.setAttribute()`) et effectue la redirection serveur (`RequestDispatcher.forward()`).

---

## Sprint 6 : Refactorisation avec `FrameListener` (`ServletContextListener`)
* **Principe :** Séparer les responsabilités au démarrage. On retire la logique lourde de scan et de cartographie du `init()` du `FrontController` pour la placer dans un Listener d'application (`FrameListener`). Au lancement du serveur, le `FrameListener` effectue le scan, construit le dictionnaire de `Mapping` et le stocke dans le `ServletContext`. Le `FrontController` devient ainsi un simple contrôleur d'exécution au runtime.

---

## Sprint 7 : Configuration et gestion de la connexion à la base de données
* **Principe :** Centraliser et fournir une gestion de la base de données au sein du framework. Les identifiants et paramètres de connexion (`db.driver`, `db.url`, `db.user`, `db.password`) sont déclarés dans le `web.xml` via des `<context-param>`. Le framework les charge au démarrage et fournit un utilitaire (`Database`) permettant aux contrôleurs de récupérer une connexion JDBC active.

---

## Sprint 8 : Support des Réponses API REST (`@RestApi`)
* **Principe :** Offrir la possibilité de retourner directement des données brutes (JSON) au lieu d'une vue HTML/JSP. Si la méthode (ou sa classe) possède l'annotation `@RestApi`, le `FrontController` shunte le mécanisme de résolution de vue (`ModelView`), sérialise la valeur de retour (en JSON via la bibliothèque Gson) et l'écrit directement dans le corps de la réponse HTTP avec le `Content-Type` approprié (`application/json`).

---

## Sprint 9-a : Traitement des formulaires paramètre par paramètre
* **Principe :** Intercepter et associer dynamiquement les champs d'un formulaire HTTP aux paramètres individuels déclarés dans la méthode du contrôleur (ex: `save(String nom, int age)`). L'annotation `@Param("nomDuChamp")` est utilisée pour faire le lien entre le nom du champ HTML et le paramètre Java.

---

## Sprint 9-b : Liaison directe d'un Formulaire vers un Objet (Object Form Binding)
* **Principe :** Mapper automatiquement l'ensemble d'un formulaire vers un objet Java (DTO). La méthode reçoit directement un objet complexe (ex: `Employe emp`). Le framework instancie cet objet par réflexion et remplit automatiquement ses attributs (y compris les objets imbriqués comme `Diplome`) à partir des champs du formulaire dont les noms correspondent aux propriétés de l'objet (ex: `emp.diplome.libelle`).

---

## Sprint 10 : Upload de fichiers
* **Principe :** Gérer l'upload de fichiers (images, CSV, PDF, etc.) de manière généralisée. 
* **Fonctionnement :** 
  * Les fichiers sont reçus via `multipart/form-data`.
  * Une classe `MapFile` encapsule le fichier (nom, type MIME, taille, tableau de bits `byte[]`).
  * Une classe `MultiFile` permet de gérer un groupe de fichiers logiquement liés (ex: un Recto et un Verso) via une clé/identifiant.
  * Le framework utilise `@Param("prefixe")` avec `MultiFile` ou `Map<String, MapFile>` pour injecter les fichiers dans la méthode du contrôleur.
  * Une configuration centralisée dans `utils/upload.properties` gère la création automatique des dossiers (images, csv, pdf, etc.) à la racine du projet web déployé.


  ## sprint 11-a : session 
* **Principe :** acces au session dans les controllers (un seule instance) : mettre le dans le context 
- un seul instance pour chaque Controller
- Mettre en parametre de methode d'action
- creer un classe session a injecter (Mapper la session) <string ; Object> ; methode :recuper variable, ajouter, supprimer
- mettre dans un methode d'action la classe 
- completer le map par Httpsession
- apres il faut mettre a jour le Httpsession apres invoker la methode

## sprint 11-b: authentification
- si le role se trouve dans la session alors elle est deja dans l'application sinon on a besoin de se connecter
- mettre une annotation qui prend le role
- mettre une variable dans le web.xml pour le role aussi(coter verification)
