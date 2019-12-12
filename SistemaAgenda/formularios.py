from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, SubmitField, IntegerField, DateField, TimeField, BooleanField
from wtforms.validators import DataRequired


class AutenticacaoForm(FlaskForm):
    username = StringField('Nome do usuário', validators=[DataRequired('Campo obrigatório')])
    senha = PasswordField('Senha', validators=[DataRequired('Campo obrigatório')])
    enviar = SubmitField('Entrar')
    voltar = SubmitField('Voltar')
    erroLogin = None


class NovaAgendaForm(FlaskForm):
    nome = StringField('Nome da Agenda', validators=[DataRequired('Campo obrigatório')])
    descricao = StringField('Descrição')
    adicionar = SubmitField('Adicionar')
    cancelar = SubmitField('Cancelar')
    removeHorario = SubmitField('X')
    adicionaHorario = SubmitField('+')
    dataInicio = DateField('Data')
    # dataFim = DateField('Data Fim')
    horarioInicio = TimeField('Hora Início')
    horarioFim = TimeField('Hora Fim')
    vagas = IntegerField('Vagas')
    ativa = BooleanField('Agenda ativa')
    horarios = []
    erroNovaAgenda = None


class InscreverForm(FlaskForm):
    nome = StringField('Nome', validators=[DataRequired('Campo obrigatório')])
    inscrever = SubmitField('Inscrever-se')