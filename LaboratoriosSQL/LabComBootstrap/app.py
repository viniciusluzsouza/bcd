from flask import Flask, render_template
from flask_bootstrap import Bootstrap
from flask_nav import Nav
from flask_nav.elements import Navbar, View

app = Flask(__name__)
app.secret_key = "String Aleatoria"

bootstrap = Bootstrap(app)
nav = Nav()
nav.init_app(app)

@nav.navigation()
def menunavbar():
    menu = Navbar('Meu site')
    menu.items = [View('Home', 'inicial'), View('Cadastro', 'inicial')]
    return menu


@app.route('/')
def inicial():
    return render_template('index.html')


if __name__ == '__main__':
    app.run(debug=True)
