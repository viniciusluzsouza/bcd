from flask import Flask, render_template, url_for, session
from werkzeug.utils import redirect

from formularios import AutenticacaoForm

app = Flask(__name__)
app.secret_key = 'String aleatória'

@app.route('/login', methods=['GET', 'POST'])
def autenticacao():
    form = AutenticacaoForm()

    if form.validate_on_submit():   # Entrou via POST
        nome = form.username.data
        senha = form.senha.data

        # TODO Verificar se o usuário e senha são validos
        session['login'] = nome

        return redirect(url_for('pessoal'))


    return render_template('login.html', formulario=form)

@app.route('/pessoal')
def pessoal():

    login = session.get('login')
    if login is None:
        return redirect(url_for('autenticacao'))

    return render_template('pessoal.html')

@app.route('/')
def inicial():
    return render_template('index.html')

if __name__ == '__main__':
    app.run(debug=True)