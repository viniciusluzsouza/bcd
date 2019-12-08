from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, SubmitField, IntegerField, DateField, TimeField, BooleanField
from wtforms.validators import DataRequired


class AutenticacaoForm(FlaskForm):
    username = StringField('Nome do usuário', validators=[DataRequired('Campo obrigatório')])
    senha = PasswordField('Senha', validators=[DataRequired('Campo obrigatório')])
    enviar = SubmitField('Entrar')

class NovaAgendaForm(FlaskForm):
    nome = StringField('Nome da Agenda', validators=[DataRequired('Campo obrigatório')])
    descricao = StringField('Descrição')
    adicionar = SubmitField('Adicionar')
    cancelar = SubmitField('Cancelar')
    removeHorario = SubmitField('X')
    adicionaHorario = SubmitField('+')
    dataInicio = DateField('Data Início', validators=[DataRequired('Campo obrigatório')])
    dataFim = DateField('Data Fim', validators=[DataRequired('Campo obrigatório')])
    horarioInicio = TimeField('Hora Início', validators=[DataRequired('Campo obrigatório')])
    horarioFim = TimeField('Hora Fim', validators=[DataRequired('Campo obrigatório')])
    vagas = IntegerField('Vagas', validators=[DataRequired('Campo Obrigatório')])
    ativa = BooleanField('Agenda ativa')
    horarios = []

    def excluiHorarioLista(self, **kwargs):
        print("executei")